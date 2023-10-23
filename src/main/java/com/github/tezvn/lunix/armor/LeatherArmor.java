package com.github.tezvn.lunix.armor;

import com.cryptomorin.xseries.XMaterial;
import com.github.tezvn.lunix.builder.LeatherArmorBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class LeatherArmor {

    private final ArmorPart part;

    private final ItemStack item;

    LeatherArmor(LeatherArmorBuilderImpl builder) {
        this.part = builder.part == null ? ArmorPart.HELMET : builder.part;
        this.item = new ItemStack(getArmor(part));
        ItemMeta meta = getArmor().getItemMeta();
        if (meta != null) {
            if (builder.name != null)
                meta.setDisplayName(builder.name);
            meta.setLore(builder.lores);
            builder.enchantments.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
            if (builder.color != null)
                leatherArmorMeta.setColor(builder.color);
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(builder.damage);
            item.setItemMeta(meta);
        }
    }

    /**
     * Get armor part
     *
     * @return Armor part
     */
    public ArmorPart getPart() {
        return part;
    }

    /**
     * Get armor item
     *
     * @return Armor item
     */
    public ItemStack getArmor() {
        return item;
    }

    /**
     * Equip armor for living entity with silent
     *
     * @param entity      Living entity to equip
     * @param silentEquip Silent if true, otherwise not
     */
    public void equip(LivingEntity entity, boolean silentEquip) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null)
            return;
        String uncapName = getPart().name().charAt(0) + getPart().name().substring(1);
        try {
            Method method = EntityEquipment.class.getDeclaredMethod("set" + uncapName, ItemStack.class, boolean.class);
            method.setAccessible(true);
            method.invoke(equipment, getArmor(), silentEquip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Attach armor into item frame with no silent
     * @param frame Item frame to attach
     */
    public void equip(ItemFrame frame) {
        equip(frame);
    }

    /**
     * Attach armor into item frame with silent
     * @param frame Item frame to equip
     * @param silent Silent if true, otherwise not
     */
    public void equip(ItemFrame frame, boolean silent) {
        frame.setItem(getArmor(), silent);
    }

    /**
     * Give armor to player by one
     *
     * @param player Player to give
     */
    public void give(Player player) {
        give(player, 1);
    }

    /**
     * Give armor to player with specific amount
     *
     * @param player Player to give
     * @param amount Amount to give
     */
    public void give(Player player, int amount) {
        IntStream.range(0, amount).forEach(i -> {
            if (!player.isOnline())
                return;
            if (player.getInventory().addItem(getArmor()).size() > 0)
                player.getWorld().dropItem(player.getLocation(), getArmor());
        });
    }

    /**
     * Spawn armor to specific location
     * @param location Location to spawn
     */
    public void spawn(Location location) {
        spawn(location);
    }

    /**
     * Spawn armor to specific location with amount
     * @param location Location to spawn
     * @param amount Item amount
     */
    public void spawn(Location location, int amount) {
        if (location.getWorld() != null)
            IntStream.range(0, amount).forEach(i -> location.getWorld().dropItem(location, getArmor()));
    }

    public static LeatherArmorBuilder builder() {
        return new LeatherArmorBuilderImpl();
    }

    private Material getArmor(ArmorPart part) {
        Optional<XMaterial> opt = XMaterial.matchXMaterial("LEATHER_" + part.name());
        return opt.map(XMaterial::parseMaterial).orElse(XMaterial.LEATHER_HELMET.parseMaterial());
    }

    private static class LeatherArmorBuilderImpl implements LeatherArmorBuilder {
        private ArmorPart part;

        private Color color;

        private String name;

        private List<String> lores = Lists.newArrayList();

        private int damage;

        private final Map<Enchantment, Integer> enchantments = Maps.newHashMap();

        public LeatherArmorBuilder setPart(ArmorPart part) {
            this.part = part;
            return this;
        }

        public LeatherArmorBuilder setColor(Color color) {
            this.color = color;
            return this;
        }

        public LeatherArmorBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public LeatherArmorBuilder setLore(String... lore) {
            this.lores = Arrays.asList(lore);
            return this;
        }

        public LeatherArmorBuilder addEnchant(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public LeatherArmorBuilder setDamage(int damage) {
            this.damage = damage;
            return this;
        }

        public LeatherArmor build() {
            return new LeatherArmor(this);
        }

    }

}
