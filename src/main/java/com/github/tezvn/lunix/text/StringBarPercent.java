package com.github.tezvn.lunix.text;

import com.github.tezvn.lunix.java.IntRange;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * A class can help create a String which represents for a process bar.
 */
public class StringBarPercent {

    /**
     * Create a process bar in {@link String} form.
     *
     * @param current           : The current value
     * @param total             : The maximum value
     * @param numberOfChars     : The number of symbols forms the bar
     * @param symbol            : The character forms the bar
     * @param completedColor    : The color of the completed part
     * @param notCompletedColor : The color of the not-completed part
     * @return
     */
    public static String createProgressBar(double current, double total, int numberOfChars, String symbol,
                                           String completedColor, String notCompletedColor) {
        float percent = (float) (current / total);
        int progressBars = (int) (numberOfChars * percent);

        return Strings.repeat(completedColor.replaceAll("&", "§") + symbol, progressBars)
                + Strings.repeat(notCompletedColor.replaceAll("&", "§") + symbol, numberOfChars - progressBars);
    }

    public static String createProgressBar(double current, double total, int numberOfChars, String symbol,
                                           String completedColor, String notCompletedColor, boolean displayPercent) {
        float percent = (float) (current / total);
        int progressBars = (int) (numberOfChars * percent);

        return Strings.repeat(completedColor.replaceAll("&", "§") + symbol, progressBars)
                + Strings.repeat(notCompletedColor.replaceAll("&", "§") + symbol, numberOfChars - progressBars)
                + (displayPercent ? " &7[" + completedColor + (percent * 100) + "%&7]" : "");

    }

    /**
     * Create a process bar in {@link String} form.
     *
     * @param current           : The current value
     * @param total             : The maximum value
     * @param numberOfChars     : The number of symbols forms the bar
     * @param symbol            : The character forms the bar
     * @param completedColor    : The color of the completed part
     * @param notCompletedColor : The color of the not-completed part
     * @see PercentColorMap
     */
    public static String createProgressBar(double current, double total, int numberOfChars, String symbol,
                                           PercentColorMap completedColor, String notCompletedColor) {
        return createProgressBar(current, total, numberOfChars, symbol, completedColor, notCompletedColor, false);
    }

    public static String createProgressBar(double current, double total, int numberOfChars, String symbol,
                                           PercentColorMap completedColor, String notCompletedColor, boolean displayPercent) {
        float percent = (float) (current / total);

        int progressBars = (int) (numberOfChars * percent);
        String currentColor = completedColor.getColorOf((int) (percent * 100), "§c");
        return Strings.repeat(currentColor + symbol, progressBars)
                + Strings.repeat(notCompletedColor.replaceAll("&", "§") + symbol, numberOfChars - progressBars)
                + (displayPercent ? " &7[" + currentColor + (percent * 100) + "%&7]" : "");
    }

    /**
     * A class that will contain a color data map to help the string process bar
     * displaying the right color when the current value reach a specific range.
     */
    public static class PercentColorMap {

        /**
         * The main map that contains color data.
         */
        private final Map<IntRange, String> colorMap = new HashMap<>();

        public PercentColorMap() {
        }

        /**
         * Append a new range with its color.
         *
         * @param range  The range.
         * @param string The color.
         */
        public PercentColorMap append(IntRange range, String string) {
            colorMap.put(range, string.replaceAll("&", "§"));
            return this;
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
    }

}