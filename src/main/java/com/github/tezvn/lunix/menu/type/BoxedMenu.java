package com.github.tezvn.lunix.menu.type;

import com.cryptomorin.xseries.XMaterial;
import com.github.tezvn.lunix.menu.SlotBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BoxedMenu<T> extends PaginationMenu<T> {

    public BoxedMenu(int page, int row, String title) {
        super(page, row, title);
    }


}