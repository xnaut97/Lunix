package com.github.tezvn.lunix.builder;

import com.github.tezvn.lunix.text.ColorMap;
import com.github.tezvn.lunix.text.ProgressBar;
import net.md_5.bungee.api.ChatColor;

public interface ProgressBarBuilder extends Builder<ProgressBar> {

    /**
     * Set current progress
     */
    ProgressBarBuilder setCurrent(double current);

    /**
     * Set total progress
     */
    ProgressBarBuilder setTotal(double total);

    /**
     * Set number of characters will display on bar
     */
    ProgressBarBuilder setQuantity(int quantity);

    /**
     * Set character that use to represent for completed progress
     */
    ProgressBarBuilder setCompletedSymbol(String symbol);

    /**
     * Set character that use to represent for incomplete progress
     */
    ProgressBarBuilder setIncompleteSymbol(String symbol);

    /**
     * Build the color map with {@link ColorMap#builder()}
     */
    ProgressBarBuilder setCompletedColor(ColorMap color);

    /**
     * Set incomplete color
     */
    ProgressBarBuilder setIncompleteColor(ChatColor color);

    /**
     * Set incomplete color with hex supported
     */
    ProgressBarBuilder setIncompleteColor(String hex);

    /**
     * Allow progress bar display exact percent on bar
     * <br>Example: [❚❚❚❚❚❚❚❚--------] [50%]
     */
    ProgressBarBuilder setDisplayPercent(boolean displayPercent);
    
}
