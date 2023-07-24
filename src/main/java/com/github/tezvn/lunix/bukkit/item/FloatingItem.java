package com.github.tezvn.lunix.bukkit.item;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


/**
 * A class that manages functions relating to {@link ItemStack} that floating in
 * {@link Item} form.
 */
public class FloatingItem {

    /**
     * Create a new instance of {@link FloatingItem}.
     */
    public static FloatingItem of(String ID, ItemStack itemStack) {
        return new FloatingItem(ID, itemStack);
    }

    /**
     * Find a {@link FloatingItem} with given ID near the given {@link Location}.
     */
    public static Optional<FloatingItem> find(Location location, String ID) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, 1, 1.5, 1)) {
            if(!(entity instanceof Item))
                continue;
            Item item = (Item) entity;
            if (item.getCustomName() != null
                    && item.getItemStack().getItemMeta().getDisplayName().startsWith(ID.toUpperCase())) {
                ItemStack itemStack = item.getItemStack();
                ItemStack stack = itemStack.clone();
                String customName = item.getCustomName();
                if (customName.equals("")) {
                    ItemMeta im = stack.getItemMeta();
                    im.setDisplayName(null);
                    stack.setItemMeta(im);
                } else {
                    ItemMeta im = stack.getItemMeta();
                    if (!customName.startsWith(""))
                        customName = ChatColor.RESET + customName;
                    im.setDisplayName(customName);
                    stack.setItemMeta(im);
                }

                FloatingItem floatingItem = new FloatingItem(ID, stack);
                floatingItem.setItem(item);
                return Optional.ofNullable(floatingItem);
            }
        }
        return Optional.empty();
    }

    /**
     * Id c{@link FloatingItem} n(phbivc{@link ItemStack} r
     * trbth
     */
    private String ID;

    /**
     * {@link Item} entity of this {@link FloatingItem}.
     */
    private Item item;

    /**
     * {@link ItemStack} form of this {@link FloatingItem}.
     */
    private ItemStack itemStack;

    public FloatingItem(String ID, ItemStack itemStack) {
        this.ID = ID;
        this.itemStack = itemStack;
    }

    public String getID() {
        return ID;
    }

    public Item getItem() {
        return item;
    }

    private void setItem(Item item) {
        this.item = item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Summon {@link FloatingItem} above the given {@link Block}.
     */
    public void summon(Block block) {
//        ItemStack clone = getItemStack().clone();
//        clone.setAmount(1);
//        String nametag = StringUtils.formatItemName(clone);
//        Item entity = block.getWorld().dropItem(block.getLocation().add(0.5, 1.2, 0.5),
//                new ItemBuilder(clone).setDisplayName(getID().toUpperCase() + " &e" + System.nanoTime()).create());
//        entity.setVelocity(new Vector(0, 0.1, 0));
//        entity.setMetadata("no_pickup", new FixedMetadataValue(TCore.getInstance(), "no_pickup"));
//        entity.setCustomNameVisible(true);
//        entity.setCustomName(nametag);
//        setItem(entity);
//        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3F, 0.3F);
    }

    /**
     * Remove the {@link FloatingItem}.
     */
    public void remove() {
        getItem().remove();
    }

}

