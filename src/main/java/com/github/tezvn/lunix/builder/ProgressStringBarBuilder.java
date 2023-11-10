package com.github.tezvn.lunix.builder;

import com.github.tezvn.lunix.text.ColorMap;
import com.github.tezvn.lunix.text.ProgressStringBar;
import net.md_5.bungee.api.ChatColor;

public interface ProgressStringBarBuilder extends Builder<ProgressStringBar> {

    /**
     * Set current progress
     */
    ProgressStringBarBuilder setCurrent(double current);

    /**
     * Set total progress
     */
    ProgressStringBarBuilder setTotal(double total);

    /**
     * Set number of characters will display on bar
     */
    ProgressStringBarBuilder setQuantity(int quantity);

    /**
     * Set character that use to represent for completed progress
     */
    ProgressStringBarBuilder setCompletedSymbol(String symbol);

    /**
     * Set character that use to represent for incomplete progress
     */
    ProgressStringBarBuilder setIncompleteSymbol(String symbol);

    /**
     * Build the color map with {@link ColorMap#builder()}
     */
    ProgressStringBarBuilder setCompletedColor(ColorMap color);

    /**
     * Set incomplete color
     */
    ProgressStringBarBuilder setIncompleteColor(ChatColor color);

    /**
     * Set incomplete color with hex supported
     */
    ProgressStringBarBuilder setIncompleteColor(String hex);

    /**
     * Allow progress bar display exact percent on bar
     * <br>Example: [❚❚❚❚❚❚❚❚--------] [50%]
     */
    ProgressStringBarBuilder setDisplayPercent(boolean displayPercent);
    
}
