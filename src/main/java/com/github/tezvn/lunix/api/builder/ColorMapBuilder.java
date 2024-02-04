package com.github.tezvn.lunix.api.builder;

import com.github.tezvn.lunix.java.IntRange;
import com.github.tezvn.lunix.text.ColorMap;
import net.md_5.bungee.api.ChatColor;

public interface ColorMapBuilder extends Builder<ColorMap> {

    ColorMapBuilder append(IntRange range, ChatColor color);

    ColorMapBuilder append(IntRange range, String hex);

}
