package com.github.tezvn.lunix.color;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public final class ColorContainer {

    private static final Map<String, ColorSet> colors = Maps.newHashMap();

    static {
        Arrays.stream(ChatColor.values()).forEach(color -> {
            ColorSet colorSet= new ColorSet(0, 0, 0);
            switch (color) {
                case DARK_BLUE -> colorSet.setBlue(170);
                case DARK_GREEN -> colorSet.setGreen(170);
                case DARK_AQUA -> colorSet.setGreen(170).setBlue(170);
                case DARK_RED -> colorSet.setRed(170);
                case DARK_PURPLE -> colorSet.setRed(170).setBlue(170);
                case GOLD -> colorSet.setRed(255).setGreen(170);
                case GRAY -> colorSet.setAll(170);
                case DARK_GRAY -> colorSet.setAll(85);
                case BLUE -> colorSet.setAll(85).setBlue(255);
                case GREEN -> colorSet.setAll(85).setGreen(255);
                case AQUA -> colorSet.setAll(255).setRed(85);
                case RED -> colorSet.setAll(85).setRed(255);
                case LIGHT_PURPLE -> colorSet.setAll(255).setGreen(85);
                case YELLOW -> colorSet.setAll(255).setBlue(85);
                case WHITE -> colorSet.setAll(255);
            }
            colors.put(String.valueOf(color.getChar()), colorSet);
        });
    }

    public static ColorSet getColor(ChatColor color) {
        return getColor(String.valueOf(color.getChar()));
    }

    @Nullable
    public static ColorSet getColor(String code) {
        return colors.getOrDefault(code, new ColorSet(255, 255, 255));
    }

}
