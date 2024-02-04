package com.github.tezvn.lunix.text;

import com.github.tezvn.lunix.api.builder.ColorMapBuilder;
import com.github.tezvn.lunix.java.IntRange;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that will contain a color data map to help the string process bar
 * displaying the right color when the current value reach a specific range.
 */
public class ColorMap {

    /**
     * The main map that contains color data.
     */
    private final Map<IntRange, String> colorMap;

    ColorMap(ColorMapBuilderImpl builder) {
        this.colorMap = builder.colorMap;
    }

    /**
     * Return the color at the given {@code percent}.<Br>
     * If that percent is not in any registered range, return the
     * {@code defaultColor}.
     */
    public String getColorOf(int percent, String defaultColor) {
        for (Map.Entry<IntRange, String> entry : colorMap.entrySet()) {
            if (entry.getKey().isInRange(percent)) return entry.getValue();
        }
        return defaultColor.replaceAll("&", "§");
    }

    public static ColorMapBuilder builder() {
        return new ColorMapBuilderImpl();
    }

    private static class ColorMapBuilderImpl implements ColorMapBuilder {

        private final Map<IntRange, String> colorMap = new HashMap<>();

        @Override
        public ColorMapBuilder append(IntRange range, ChatColor color) {
            colorMap.entrySet().stream().filter(entry -> {
                IntRange r = entry.getKey();
                return r.isInRange(r.getMin()) || r.isInRange(r.getMax());
            }).findAny().ifPresentOrElse(entry -> {}, () -> colorMap.put(range, color.getName()));
            return this;
        }

        @Override
        public ColorMapBuilder append(IntRange range, String hex) {
            if(!(hex.startsWith("#") && hex.length() == 7))
                throw new IllegalArgumentException("Invalid hex color");
            colorMap.entrySet().stream().filter(entry -> {
                IntRange r = entry.getKey();
                return r.isInRange(r.getMin()) || r.isInRange(r.getMax());
            }).findAny().ifPresentOrElse(entry -> {}, () -> colorMap.put(range, hex));
            return this;
        }

        @Override
        public ColorMap build() {
            return new ColorMap(this);
        }

    }

}