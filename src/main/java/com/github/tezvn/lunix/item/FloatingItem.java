package com.github.tezvn.lunix.item;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;


/**
 * A class that manages functions relating to {@link ItemStack} that floating in
 * {@link Item} form.
 */
@Getter
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
        AtomicReference<Optional<FloatingItem>> optional = new AtomicReference<>(Optional.empty());
        World world = location.getWorld();
        if(world != null) {
            world.getNearbyEntities(location, 1, 1, 1).stream()
                    .filter(item -> item instanceof Item && item.hasMetadata("id") && item.getMetadata("id").get(0).asString().equals(ID))
                    .map(entity -> (Item) entity)
                    .findFirst().ifPresent(item -> {
                        String id = item.getMetadata("id").get(0).asString();
                        ItemStack stack = item.getItemStack();
                        optional.set(Optional.of(FloatingItem.of(id, stack)));
                    });
        }
        return optional.get();
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

    private void setItem(Item item) {
        this.item = item;
    }

    /**
     * Summon {@link FloatingItem} above the given {@link Block}.
     */
    public void summon(Block block, Plugin plugin) {
        ItemStack clone = getItemStack().clone();
        clone.setAmount(1);
        Item entity = block.getWorld().dropItem(block.getLocation().add(0.5, 1.2, 0.5), clone);
        entity.setVelocity(new Vector(0, 0.1, 0));
        entity.setCustomNameVisible(true);
        entity.setPickupDelay(Integer.MAX_VALUE);
        entity.setCustomName(getDisplayName());
        entity.setMetadata("id", new FixedMetadataValue(plugin, getID()));
        setItem(entity);
        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3F, 0.3F);
    }

    /**
     * Remove the {@link FloatingItem}.
     */
    public void remove() {
        if (getItem() != null)
            getItem().remove();
    }

    private String getDisplayName() {
        ItemMeta meta = getItemStack().getItemMeta();
        return meta != null && meta.hasDisplayName()
                ? meta.getDisplayName()
                : Arrays.stream(getItemStack().getType().name().split("_"))
                .map(s -> s.charAt(0) + s.substring(1))
                .collect(Collectors.joining(" "));
    }

}

