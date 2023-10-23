package com.github.tezvn.lunix.builder;

import com.github.tezvn.lunix.armor.ArmorPart;
import com.github.tezvn.lunix.armor.LeatherArmor;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;

public interface LeatherArmorBuilder extends Builder<LeatherArmor> {

    LeatherArmorBuilder setPart(ArmorPart part);

    LeatherArmorBuilder setColor(Color color);

    LeatherArmorBuilder setName(String name);

    LeatherArmorBuilder setLore(String... lore);

    LeatherArmorBuilder addEnchant(Enchantment enchantment, int level);

    LeatherArmorBuilder setDamage(int damage);

}
