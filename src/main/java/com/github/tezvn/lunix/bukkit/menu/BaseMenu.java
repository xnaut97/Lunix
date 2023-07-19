package com.github.tezvn.lunix.bukkit.menu;

import com.cryptomorin.xseries.XMaterial;
import com.github.tezvn.lunix.bukkit.item.ItemCreator;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class BaseMenu implements InventoryHolder {

    private static Plugin plugin;

    private final Inventory inventory;

    private final List<UUID> players;

    private final String title;

    private String lastTitle;

    private MenuElement[] elements;

    public BaseMenu(int size, String title) {
        size = 9 * Math.max(1, Math.min(6, size));
        if (title == null)
            title = "";
        this.inventory = Bukkit.createInventory(this, size, title.replace("&", "§"));
        this.players = Lists.newArrayList();
        this.elements = new MenuElement[size];
        this.title = title;
    }

    public static void forceCloseAll() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (inventory.getHolder() instanceof BaseMenu)
                player.closeInventory();
        });
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName()
                .substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
    }

    private static int getVersionNumber() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }

    public static void register(Plugin instance) {
        plugin = instance;
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getCurrentItem() == null)
                    return;
                Inventory inv = e.getClickedInventory();
                try {
                    if (inv.getHolder() != null && inv.getHolder() instanceof BaseMenu)
                        ((BaseMenu) inv.getHolder()).onClick(e);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                Inventory inv = e.getInventory();
                try {
                    if (inv.getHolder() != null && inv.getHolder() instanceof BaseMenu)
                        ((BaseMenu) inv.getHolder()).onClose(e);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, plugin);

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onOpen(InventoryOpenEvent e) {
                Inventory inv = e.getInventory();
                try {
                    if (inv.getHolder() != null && inv.getHolder() instanceof BaseMenu)
                        ((BaseMenu) inv.getHolder()).onOpen(e);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, plugin);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public MenuElement[] getElements() {
        return elements;
    }

    public void pushElements(MenuElement[] elements) {
        this.elements = elements;
    }

    public String getTitle() {
        return this.title;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setTitle(Player player, String title, boolean async, long ticks) {
        setTitle(player, title);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                setTitle(player, getTitle());
            }
        };
        if (async)
            runnable.runTaskLaterAsynchronously(plugin, ticks);
        else
            runnable.runTaskLater(plugin, ticks);
    }

    public void setTitle(Player player, String title) {
        InventoryUpdate.updateInventory(getPlugin(), player, title);
    }

    public void onClick(InventoryClickEvent event) {
        MenuElement element = getElements()[event.getSlot()];
        if (element == null)
            return;
        element.onClick(event);
        element.getSound().play((Player) event.getWhoClicked());
    }

    public void onOpen(InventoryOpenEvent event) {
        this.players.add(event.getPlayer().getUniqueId());
        onOpenActions(event);
    }

    public void onClose(InventoryCloseEvent event) {
        this.players.remove(event.getPlayer().getUniqueId());
//        for (InventoryElement element : getElements()) {
//            if(element == null || element.getItem() == null)
//                continue;
//            element.onClose(event);
//        }
        onCloseActions(event);
    }

    /**
     * Push element to specific slot in inventory
     *
     * @param slot    Slot to push element
     * @param element Element to push
     * @return True if success
     * <br>Otherwise false if slot < 0 or slot > inventory size
     */
    public boolean pushElement(int slot, MenuElement element) {
        if (slot < 0 || slot > getInventory().getSize())
            return false;
        getInventory().setItem(slot, element.getItem());
        getElements()[slot] = element;
        return true;
    }

    /**
     * Open current inventory for player
     *
     * @param player Player to open
     * @return True if success
     * <br>Otherwise false if player is offline
     */
    public boolean open(Player player) {
        return open(player, false, this);
    }

    /**
     * Open other inventory for player
     *
     * @param player    Player to open
     * @param inventory Inventory to open
     * @return True if success
     * <br>Otherwise false if player is offline
     */
    public boolean open(Player player, boolean async, BaseMenu inventory) {
        if (!player.isOnline())
            return false;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(inventory.getInventory());
            }
        };
        if (async)
            runnable.runTaskAsynchronously(plugin);
        else
            runnable.runTask(plugin);
        return true;
    }

    public boolean close(Player player) {
        return close(player, false);
    }

    /**
     * Close inventory for player
     *
     * @param player Player to close
     * @return True if success
     * <br>Otherwise false if player is opening other inventory
     */
    public boolean close(Player player, boolean async) {
        if (!this.players.contains(player.getUniqueId()))
            return false;
        if (async) {
            player.closeInventory();
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            }.runTask(plugin);
        }
        this.players.remove(player.getUniqueId());
        return true;
    }

    /**
     * Force close for all player is opening this inventory
     */
    public void closeAll(boolean async) {
        for (UUID uuid : this.players) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (!player.isOnline())
                continue;
            close(player.getPlayer(), async);
        }
        this.players.clear();
    }

    public boolean recalculate(int page, int listSize, int slots) {
        return page != 0 && listSize <= page * slots;
    }

    public boolean isMax(int page, int listSize, int slots) {
        return (page + 1) * slots >= listSize;
    }

    /**
     * Triggered when open inventory
     */
    public abstract void onOpenActions(InventoryOpenEvent event);

    /**
     * Triggered when close inventory
     *
     * @param event
     */
    public abstract void onCloseActions(InventoryCloseEvent event);

    public abstract static class RunnableMenu extends BaseMenu {

        private final AtomicBoolean running;

        private final boolean async;

        private final int ticks;

        public RunnableMenu(int size, String title, int ticks, boolean async) {
            super(size, title);
            this.ticks = ticks;
            this.async = async;
            this.running = new AtomicBoolean(true);
            init();
        }

        public boolean isAsync() {
            return async;
        }

        public int getTicks() {
            return ticks;
        }

        private void init() {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running.get()) {
                        cancel();
                        onCancel();
                        return;
                    }
                    onTick();
                }
            };
            if (async)
                runnable.runTaskTimerAsynchronously(plugin, 0, ticks);
            else
                runnable.runTaskTimer(plugin, 0, ticks);
        }

        @Override
        public final void onOpen(InventoryOpenEvent event) {
            running.set(true);
            onOpenActions(event);
        }

        @Override
        public void onClose(InventoryCloseEvent event) {
            running.set(false);
            onCloseActions(event);
        }

        public abstract void onTick();

        public void onCancel() {
        }

    }

    @Deprecated
    public abstract static class PagedMenu extends BaseMenu {

        private final int size;
        protected ItemStack background;
        protected ItemStack toolBar;
        protected int[] itemSlots;
        private int page;

        public PagedMenu(int page, int row, String title) {
            super(9 * row, title);
            this.size = 9 * row;
            this.page = page;
            this.itemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8,
                    9, 10, 11, 12, 13, 14, 15, 16, 17,
                    18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, 28, 29, 30, 31, 32, 33, 34, 35,
                    36, 37, 38, 39, 40, 41, 42, 43, 44};
            setToolBar(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setToolBar(ItemStack item) {
            this.toolBar = item;
            for (int i = size - 9; i < size; i++) {
                pushElement(i, new MenuElement(item, " ") {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onToolBarClick(event);
                    }
                });
            }
        }

        public void onToolBarClick(InventoryClickEvent event) {
            event.setCancelled(true);
        }

        public final void setNextButton(int slot, int listSize, String name, String[] lore, Runnable onFailure) {
            boolean isMax = isMax(getPage(), listSize, 45);
            int max = listSize / 45;
            if (!isMax) {
                ItemStack item = new ItemCreator(XMaterial.PLAYER_HEAD.parseItem())
                        .setDisplayName(name)
                        .addLore(lore)
                        .setTexture("4ae29422db4047efdb9bac2cdae5a0719eb772fccc88a66d912320b343c341")
                        .build();
                pushElement(slot, new MenuElement(item) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        setPage(Math.min(max, getPage() + 1));
                        onNextPage(event);
                    }
                });
            } else {
                if (onFailure != null)
                    onFailure.run();
            }
        }

        public abstract void onNextPage(InventoryClickEvent event);

        public final void setPreviousButton(int slot, String name, String[] lore, Runnable onFailure) {
            if (getPage() > 0) {
                ItemStack item = new ItemCreator(XMaterial.PLAYER_HEAD.parseItem())
                        .setDisplayName(name)
                        .addLore(lore)
                        .setTexture("9945491898496b136ffaf82ed398a54568289a331015a64c843a39c0cbf357f7")
                        .build();
                pushElement(slot, new MenuElement(item) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                        setPage(Math.max(0, getPage() - 1));
                        onPreviousPage(event);
                    }
                });
            } else {
                if (onFailure != null)
                    onFailure.run();
            }
        }

        public abstract void onPreviousPage(InventoryClickEvent event);

        public final void setPageInfo(int slot, String name) {
            ItemStack item = new ItemCreator(Objects.requireNonNull(XMaterial.PAPER.parseItem()))
                    .setDisplayName(name).build();
            pushElement(slot, new MenuElement(item) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    onPageClick(event);
                }
            });
        }

        public void onPageClick(InventoryClickEvent event) {
        }

        public int[] getItemSlots() {
            return itemSlots;
        }

    }

    public abstract static class BoxedMenu extends PagedMenu {

        private final int[] borderSlots = {36, 27, 18, 9,
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                17, 26, 35, 44};
        private ItemStack boxedMaterial;


        public BoxedMenu(int page, int row, String title) {
            super(page, row, title);
            this.itemSlots = new int[]{10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34,
                    37, 38, 39, 40, 41, 42, 43};
            setBorder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
            setToolBar(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        }

        public void setBorder(ItemStack item) {
            this.boxedMaterial = item;
            for (int i : getBorderSlots()) {
                pushElement(i, new MenuElement(item, " ") {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        onBorderClick(event);
                    }
                });
            }
        }

        public void onBorderClick(InventoryClickEvent event) {
        }

        public int[] getBorderSlots() {
            return borderSlots;
        }
    }

    public abstract static class TradingMenu extends BaseMenu {

        private final Player sender, target;
        private final int[] leftSlots = {
                0, 1, 2, 3,
                9, 10, 11, 12,
                18, 19, 20, 21,
                27, 28, 29, 30,
                36, 37, 38, 39
        };
        private final int[] rightSlots = {
                5, 6, 7, 8,
                14, 15, 16, 17,
                23, 24, 26, 26,
                32, 33, 34, 35,
                41, 42, 43, 44
        };
        private final int[] leftToolBar = {45, 46, 47, 48};
        private final int[] rightToolBar = {50, 51, 52, 53};
        private final int[] borderSlots = {4, 13, 22, 31, 40, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        private TradingMenu opposite;

        public TradingMenu(Player sender, Player target, int size, String title) {
            super(size, title);
            this.sender = sender;
            this.target = target;
        }

        @Override
        public final void onClose(InventoryCloseEvent event) {

            onCloseActions(event);
        }
    }

    public static abstract class PaginationMenu<T> extends BaseMenu {

        private final int[] borderSlots =
                {36, 27, 18, 9,
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        17, 26, 35, 44,
                        45, 46, 47, 48, 49, 50, 51, 52, 53};
        private final int[] itemSlots =
                {10, 11, 12, 13, 14, 15, 16,
                        19, 20, 21, 22, 23, 24, 25,
                        28, 29, 30, 31, 32, 33, 34,
                        37, 38, 39, 40, 41, 42, 43};
        private int page;

        private Player player;

        private boolean updateInstantly = true;

        private long updateTick = 1;

        private BukkitRunnable runnable;

        private boolean exit;

        public PaginationMenu(int page, int row, String title) {
            super(9 * row, title);
            this.page = page;
            setup();
        }

        private void setupBackground() {
            for (int slot : borderSlots) {
                pushElement(slot, new MenuElement(getBorderItem(), " ") {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            }
            for (int slot : itemSlots) {
                pushElement(slot, new MenuElement(getPlaceholderItem(), " ") {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            }
        }

        private void setup() {
            setupBackground();
            this.runnable = new BukkitRunnable() {
                private long count = 0;

                @Override
                public void run() {
                    count++;
                    if (count == Long.MAX_VALUE)
                        count = 0;
                    if (count % updateTick > 0)
                        return;
                    if (count / updateTick > 1 && !updateInstantly)
                        return;
                    update();
                }
            };
        }

        public void update() {
            List<T> validated = getObjects().stream()
                    .filter(this::onValidate).collect(Collectors.toList());
            for (int i = 0; i < getItemSlots().length; i++) {
                int index = (getPage() + 1) * i;
                if (index >= validated.size()) {
                    pushElement(getItemSlots()[i], new MenuElement(fillOtherSlotWhenFull() == null
                            ? new ItemStack(Material.AIR) : fillOtherSlotWhenFull(), " ") {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                        }
                    });
                    continue;
                }
                T object = validated.get(index);

                pushElement(getItemSlots()[i], getObjectItem(object));
            }

            pushElement(getPreviousButtonSlot(), getPage() == 0
                    ? new MenuElement(getBorderItem(), " ") {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            }
                    : new MenuElement(getPreviousButton()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    onPreviousButtonClick(event);
                    previous();
                }
            });

            pushElement(getInfoButtonSlot(), new MenuElement(getInfoButton()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    onInfoButtonClick(event);
                }
            });

            boolean reachMax = getPage() == getMaxPage();
            pushElement(getNextButtonSlot(), reachMax
                    ? new MenuElement(getBorderItem(), " ") {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                }
            }
                    : new MenuElement(getNextButton()) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    onNextButtonClick(event);
                    next();
                }
            });
            onIndexComplete();
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            super.onOpen(event);
        }

        public Player getPlayer() {
            return player;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = Math.max(0, Math.min(getMaxPage(), page));
        }

        public void next() {
            this.page = Math.min(page + 1, getMaxPage());
        }

        public void previous() {
            this.page = Math.max(page - 1, 0);
        }

        public int getMaxPage() {
            return (int) getObjects().stream().filter(this::onValidate).count()
                    / ((getPage() + 1) * getItemSlots().length);
        }

        public int[] getBorderSlots() {
            return borderSlots;
        }

        public ItemStack getBorderItem() {
            return XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        }

        public int[] getItemSlots() {
            return itemSlots;
        }

        public ItemStack getPlaceholderItem() {
            return XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        }

        @Override
        public void onOpenActions(InventoryOpenEvent event) {
            this.player = (Player) event.getPlayer();
            if (this.runnable != null)
                this.runnable.runTaskTimerAsynchronously(plugin, 0, 1);
        }

        @Override
        public void onCloseActions(InventoryCloseEvent event) {
            Player player = (Player) event.getPlayer();
            if (getPlayer() != null && player.getUniqueId().equals(getPlayer().getUniqueId()))
                exit = true;
            if (this.runnable != null)
                this.runnable.cancel();
        }

        public int getPreviousButtonSlot() {
            return 48;
        }

        public ItemStack getPreviousButton() {
            return new ItemCreator(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()))
                    .setDisplayName("&e« TRANG TRƯỚC")
                    .addLore("&7Trở về trang &e" + getPage())
                    .setTexture("9945491898496b136ffaf82ed398a54568289a331015a64c843a39c0cbf357f7")
                    .build();
        }

        public void onPreviousButtonClick(InventoryClickEvent event) {
        }

        public int getInfoButtonSlot() {
            return 49;
        }

        public ItemStack getInfoButton() {
            return new ItemCreator(Objects.requireNonNull(XMaterial.PAPER.parseItem()))
                    .setDisplayName("&eTrang " + (getPage() + 1)).build();
        }

        public void onInfoButtonClick(InventoryClickEvent event) {
        }

        public int getNextButtonSlot() {
            return 50;
        }

        public ItemStack getNextButton() {
            return new ItemCreator(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()))
                    .setDisplayName("&eTRANG KẾ »")
                    .addLore("&7Qua trang &e" + (getPage() + 2))
                    .setTexture("4ae29422db4047efdb9bac2cdae5a0719eb772fccc88a66d912320b343c341")
                    .build();
        }

        public void setUpdateInstantly(boolean updateInstantly) {
            this.updateInstantly = updateInstantly;
        }

        public boolean isUpdateInstantly() {
            return updateInstantly;
        }

        public long getUpdateTick() {
            return updateTick;
        }

        public void setUpdateTick(long updateTick) {
            this.updateTick = updateTick;
        }

        public void onNextButtonClick(InventoryClickEvent event) {
        }

        public boolean onValidate(T object) {
            return true;
        }

        public boolean isExit() {
            return exit;
        }

        public abstract List<T> getObjects();

        public abstract MenuElement getObjectItem(T object);

        public ItemStack fillOtherSlotWhenFull() {
            return null;
        }

        public void onIndexComplete() {
        }
    }

}
