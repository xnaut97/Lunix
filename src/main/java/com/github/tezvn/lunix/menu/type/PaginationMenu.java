package com.github.tezvn.lunix.menu.type;

import com.cryptomorin.xseries.XMaterial;
import com.github.tezvn.lunix.item.ItemCreator;
import com.github.tezvn.lunix.menu.Menu;
import com.github.tezvn.lunix.menu.MenuElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PaginationMenu<T> extends Menu {

    private int[] borderSlots =
            {36, 27, 18, 9,
                    0, 1, 2, 3, 4, 5, 6, 7, 8,
                    17, 26, 35, 44,
                    45, 46, 47, 48, 49, 50, 51, 52, 53};
    private int[] itemSlots =
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
                .filter(this::onValidate).toList();
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
            this.runnable.runTaskTimerAsynchronously(getPlugin(), 0, 1);
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
                .setDisplayName("&e« PREVIOUS")
                .addLore("&7Back to page &e" + getPage())
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

    public void setItemSlots(int[] itemSlots) {
        this.itemSlots = itemSlots;
    }

    public void setBorderSlots(int[] borderSlots) {
        this.borderSlots = borderSlots;
    }

    public void onIndexComplete() {
    }
}