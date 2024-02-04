package com.github.tezvn.lunix.api.builder;

import org.bukkit.ChatColor;

public interface GradientBuilder<T> extends Builder<T> {

    String getLeftColor();

    String getRightColor();

    GradientBuilder<T> setLeftColor(String hex);

    GradientBuilder<T> setLeftColor(ChatColor color);

    GradientBuilder<T> setRightColor(String hex);

    GradientBuilder<T> setRightColor(ChatColor color);
    
}
