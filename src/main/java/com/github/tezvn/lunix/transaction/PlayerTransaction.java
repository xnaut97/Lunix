package com.github.tezvn.lunix.transaction;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.github.tezvn.lunix.chat.v1.MessageUtils;
import com.github.tezvn.lunix.item.ItemCreator;
import com.github.tezvn.lunix.menu.DraggableElement;
import com.github.tezvn.lunix.menu.Menu;
import com.github.tezvn.lunix.menu.MenuElement;
import com.github.tezvn.lunix.menu.SlotBuilder;
import com.github.tezvn.lunix.menu.SlotBuilder.SlotIndex;
import com.github.tezvn.lunix.menu.SlotBuilder.SlotType;
import com.github.tezvn.lunix.queue.ThreadWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerTransaction {

    private final Plugin plugin;

    private Player owner;

    private Player opposite;

    private final Map<UUID, TradingMenu> menus = Maps.newHashMap();

    private Listener listener;

    public PlayerTransaction(Plugin plugin) {
        this.plugin = plugin;
    }

    public PlayerTransaction setPlayer(Player player) {
        this.owner = player;
        this.menus.put(player.getUniqueId(), new TradingMenu(this, this.owner, Side.PLAYER));
        return this;
    }

    public PlayerTransaction setOpposite(Player player) {
        this.opposite = player;
        this.menus.put(player.getUniqueId(), new TradingMenu(this, this.opposite, Side.OPPOSITE));
        return this;
    }

    public void start() {
        registerListener();
        this.menus.values().forEach(menu -> menu.open(menu.getPlayer()));
    }

    public void cancel() {
        closeMenu();
        unregisterListener();
    }

    public boolean complete() {
        return complete(false);
    }

    public boolean complete(boolean force) {
        boolean allConfirmed = this.menus.values().stream().allMatch(TradingMenu::isConfirmed);
        if (!force && !allConfirmed) return false;

        this.menus.values().stream().peek(menu -> menu.confirmed = true).forEach(menu -> {
            menu.close(menu.getPlayer());
            XSound.ENTITY_PLAYER_LEVELUP.play(menu.player);
            MessageUtils.sendMessage(menu.getPlayer(), "&aTransaction complete !");
        });
        cancel();
        return true;
    }


    private void closeMenu() {
        menus.values().forEach(menu -> {
            if (menu.isClosed()) return;
            menu.close(menu.getPlayer());
        });
    }

    private void unregisterListener() {
        if (listener != null)
            HandlerList.unregisterAll(listener);
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(listener = new TransactionListener(this), plugin);
    }

    private TradingMenu getMenu(Side side) {
        return this.menus.values().stream().filter(menu -> menu.side == side)
                .findAny().orElse(null);
    }

    private static class TradingMenu extends Menu {

        private final List<SlotIndex> leftSlots = new SlotBuilder().split(1, 4, 1, 3);

        private final List<SlotIndex> rightSlots = new SlotBuilder().split(1, 4, 5, 8);

        private final List<SlotIndex> leftStatusBar = new SlotBuilder().split(5, 5, 1, 3);

        private final List<SlotIndex> rightStatusBar = new SlotBuilder().split(5, 5, 5, 7);

        private final List<SlotIndex> toolBar = new SlotBuilder().split(1, 4, 4, 4);

        private final List<Integer> border;

        private final Player player;

        private final PlayerTransaction transaction;

        private final Side side;

        private boolean confirmed;

        public TradingMenu(PlayerTransaction transaction, Player player, Side side) {
            super(6, "");
            this.transaction = transaction;
            this.side = side;
            this.player = player;
            border = new SlotBuilder.Boxed().build(SlotBuilder.BuildMode.INSIDE).stream()
                    .map(s -> s.get(SlotType.BUKKIT)).collect(Collectors.toList());
            border.addAll(Lists.newArrayList(13, 22, 31, 40));
            setup();
        }

        public PlayerTransaction getTransaction() {
            return this.transaction;
        }

        public Player getPlayer() {
            return player;
        }

        public List<SlotIndex> getLeftSlots() {
            return leftSlots;
        }

        public List<SlotIndex> getRightSlots() {
            return rightSlots;
        }

        @Override
        public void onOpenActions(InventoryOpenEvent event) {
        }

        @Override
        public void onCloseActions(InventoryCloseEvent event) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (getOppositeMenu().isClosed()) {
                    getTransaction().menus.clear();
                    return;
                }
                getTransaction().cancel();
            }, 1);
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        private void setup() {
            this.leftSlots.stream().map(i -> i.get(SlotType.BUKKIT)).forEach(i -> {
                pushElement(i, new DraggableElement(new ItemStack(Material.AIR)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (!isOwnerClicking(event.getWhoClicked())) {
                            event.setCancelled(true);
                            return;
                        }

                        updateItem(event.getSlot());
                    }

                    @Override
                    public void onDrag(int slot, @Nullable ItemStack oldCursor, @Nullable ItemStack newCursor) {
                        updateItem(slot);
                    }

                    private void updateItem(int slot) {
                        getLeftSlots().stream().filter(s -> s.get(SlotType.BUKKIT) == slot).findAny().ifPresentOrElse(
                                s -> Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(),
                                        () -> getOppositeMenu().getInventory().setItem(slot + 4, getInventory().getItem(slot)), 1)
                                ,
                                () -> {
                                });
                    }

                    @Override
                    public void onClose(InventoryCloseEvent event) {
                        // If both did not success the trade
                        if (isConfirmed() && getOppositeMenu().isConfirmed()) return;
                        dropItem(player, getInventory().getItem(i));
                    }
                }.setDraggable(true));
            });
            this.rightSlots.stream().map(i -> i.get(SlotType.BUKKIT)).forEach(i -> {
                pushElement(i, new MenuElement(new ItemStack(Material.AIR)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }

                    @Override
                    public void onClose(InventoryCloseEvent event) {
                        // If both success the trade
                        if (isConfirmed() && getOppositeMenu().isConfirmed())
                            dropItem(player, getInventory().getItem(i));
                    }
                });
            });
            setupBackground();
            setupButton();
            setupPlayerHead();
        }

        private void setupBackground() {
            IntStream.range(0, getInventory().getSize()).forEach(i -> {
                if (border.contains(i)) return;
                pushElement(i, new MenuElement(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), " ") {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            });
        }

        private void setupPlayerHead() {
            new ThreadWorker().submit(() -> {
                pushElement(2, new MenuElement(new ItemCreator(
                        Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()))
                        .setDisplayName("&a&lYou")
                        .setTexture(side == Side.PLAYER ? transaction.owner : transaction.opposite)
                        .build()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });

                pushElement(6, new MenuElement(new ItemCreator(
                        Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()))
                        .setDisplayName("&6&l" + getOppositeMenu().getPlayer().getName())
                        .setTexture(side == Side.PLAYER ? transaction.opposite : transaction.owner)
                        .build()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            });
        }

        private void setupButton() {
            toolBar.forEach(s -> {
                int slot = s.get(SlotType.BUKKIT);
                switch (s.get(SlotType.ROW)) {
                    case 1 -> {
                        setupConfirmButton(slot);
                    }

                    case 4 -> pushElement(slot, new MenuElement(new ItemCreator(
                            Objects.requireNonNull(XMaterial.BARRIER.parseItem()))
                            .setDisplayName("&c&lCANCEL")
                            .addLore("&7Leave this transaction.")
                            .build()) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                            if (!isOwnerClicking(event.getWhoClicked())) return;
                            getTransaction().cancel();
                        }
                    });

                    default -> pushElement(slot, new MenuElement(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), " ") {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                        }
                    });
                }
            });
        }

        private void setupConfirmButton(int slot) {
            pushElement(slot, new MenuElement(new ItemCreator(
                    Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()))
                    .setDisplayName(isConfirmed() ? "&a&lCONFIRMED ✔" : "&a&lCONFIRM")
                    .addLore(isConfirmed()
                            ? "&7Waiting for the opposite"
                            : "&7Click to confirm your trade")
                    .setTexture(isConfirmed()
                            ? "4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4"
                            : "ffec3d25ae0d147c342c45370e0e43300a4e48a5b43f9bb858babff756144dac")
                    .build()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    if (!isOwnerClicking(event.getWhoClicked())) return;

                    event.setCancelled(true);
                    confirmed = !isConfirmed();
                    setupConfirmButton(slot);

                    if (getTransaction().complete()) return;
                    updateStatusBar();
                }
            });
        }

        private void updateStatusBar() {
            ItemCreator creator = new ItemCreator(Objects.requireNonNull(
                    isConfirmed()
                            ? XMaterial.GREEN_STAINED_GLASS_PANE.parseItem()
                            : XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
            creator.setDisplayName(isConfirmed() ? "&a&lCONFIRMED ✔" : " ");
            if (isConfirmed())
                creator.addLore("&7Waiting for your confirmation");

            TradingMenu oppositeMenu = getOppositeMenu();
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(oppositeMenu.player);
            oppositeMenu.rightStatusBar.forEach(slotIndex -> {
                int slot = slotIndex.get(SlotType.BUKKIT);
                oppositeMenu.pushElement(slot, new MenuElement(creator.build()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            });

            this.leftStatusBar.forEach(slotIndex -> {
                int slot = slotIndex.get(SlotType.BUKKIT);
                pushElement(slot, new MenuElement(creator.build()) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            });
        }

        private void dropItem(Player player, ItemStack item) {
            if (item == null) return;
            if (player.getInventory().addItem(item).size() > 0)
                player.getWorld().dropItem(player.getLocation(), item);
        }

        private boolean isOwnerClicking(HumanEntity entity) {
            return entity.getUniqueId().equals(this.player.getUniqueId());
        }

        private TradingMenu getOppositeMenu() {
            return getTransaction().getMenu(side.getOpposite());
        }

    }

    @Getter
    private class TransactionListener implements Listener {

        private final PlayerTransaction transaction;

        public TransactionListener(PlayerTransaction transaction) {
            this.transaction = transaction;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory == null) return;
            if (clickedInventory.getType() != InventoryType.PLAYER) return;

            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;

            if (!event.isShiftClick()) return;

            menus.values().stream()
                    .filter(menu -> menu.getPlayer().getUniqueId().equals(event.getWhoClicked().getUniqueId()))
                    .findAny().ifPresentOrElse(menu -> {
                        menu.getLeftSlots().stream().map(s -> s.get(SlotType.BUKKIT)).forEach(slot -> {
                            synchronized (this) {
                                ItemStack cItem = menu.getInventory().getItem(slot);
                                boolean stackable = cItem != null && cItem.getType() == item.getType() && cItem.getAmount() < cItem.getMaxStackSize();
                                ItemStack toUpdate = cItem;
                                if(stackable) {
                                    // Found exist item that can be stacked
                                    ItemStack exist = menu.getInventory().getItem(slot);
                                    if (exist == null) return;

                                    int amount = item.getAmount();
                                    int existAmount = exist.getAmount();
                                    int maxAmount = exist.getMaxStackSize();

                                    boolean oversize = amount + existAmount <= maxAmount;
                                    exist.setAmount(oversize ? maxAmount : amount + existAmount);
                                    item.setAmount(oversize ? amount + existAmount - maxAmount : item.getAmount());

                                    //Update opposite player
                                    toUpdate = exist;
                                }
                                menu.getOppositeMenu().getInventory().setItem(slot + 4, toUpdate);
                            }
                        });
                    }, () -> event.setCancelled(true));

//            menus.values().stream()
//                    .filter(menu -> menu.getPlayer().getUniqueId().equals(event.getWhoClicked().getUniqueId()))
//                    .findAny().ifPresentOrElse(
//                            menu -> menu.getLeftSlots().stream().map(s -> s.get(SlotType.BUKKIT)).filter(slot -> {
//                                ItemStack cItem = menu.getInventory().getItem(slot);
//                                return cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(item);
//                            }).findAny().ifPresentOrElse(slot -> {
//                                    },
//                                    // If there is no similar item to be stacked then gotta find a new free slot
//                                    () -> menu.getLeftSlots().stream().map(s -> s.get(SlotType.BUKKIT))
//                                            .filter(s -> menu.getInventory().getItem(s) == null)
//                                            .findAny().ifPresentOrElse(slot -> {
//                                                        // Find any free slots
//                                                        //If stack is greater than max stack then continue to loop
//                                                        ItemStack toUpdate = item;
//                                                        if (item.getAmount() > item.getMaxStackSize()) {
//                                                            ItemStack clone = item.clone();
//                                                            clone.setAmount(item.getMaxStackSize());
//                                                            item.setAmount(item.getAmount() - item.getMaxStackSize());
//                                                            toUpdate = clone;
//                                                        }
//                                                        menu.getInventory().setItem(slot, toUpdate);
//                                                        //Update opposite player
//                                                        menu.getOppositeMenu().getInventory().setItem(slot + 4, toUpdate);
//                                                    }
//                                                    // No slot for item to be pushed up
//                                                    , () -> event.setCancelled(true))),
//                            () -> event.setCancelled(true));
        }

    }


    public enum Side {
        PLAYER,
        OPPOSITE;

        public Side getOpposite() {
            return this == Side.OPPOSITE ? PLAYER : OPPOSITE;
        }
    }

}

