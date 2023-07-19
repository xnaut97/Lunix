package com.github.tezvn.lunix.bukkit.menu;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuElement {

    private final ItemStack item;

    private ElementSound sound = ElementSound.useDefault();

    public MenuElement(Material item, int amount) {
        this.item = new ItemStack(item, amount);
    }

    public MenuElement(Material item) {
        this.item = new ItemStack(item);
    }

    public MenuElement(ItemStack item, String name) {
        this.item = item;
        ItemMeta im = this.item.getItemMeta();
        if (im != null) {
            im.setDisplayName(name);
            this.item.setItemMeta(im);
        }
    }

    public MenuElement(Material item, String name) {
        this.item = new ItemStack(item);
        ItemMeta im = this.item.getItemMeta();
        if (im != null) {
            im.setDisplayName(name);
            this.item.setItemMeta(im);
        }
    }

    public MenuElement(ItemStack item) {
        this.item = item;
    }

    public void onClick(InventoryClickEvent event) {
    }

    public void onClose(InventoryCloseEvent event) {
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ElementSound getSound() {
        return sound;
    }

    public MenuElement setSound(XSound sound) {
        return setSound(sound, 1, 1);
    }

    public MenuElement setSound(XSound sound, float volume, float pitch) {
        this.sound = new ElementSound(sound, volume, pitch);
        return this;
    }

}