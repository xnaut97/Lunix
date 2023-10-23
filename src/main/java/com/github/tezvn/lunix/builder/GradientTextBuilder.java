package com.github.tezvn.lunix.builder;

import com.github.tezvn.lunix.color.GradientBuilder;
import com.github.tezvn.lunix.text.GradientText;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;

public interface GradientTextBuilder extends GradientBuilder<GradientText> {

    @Nullable
    String getText();

    GradientTextBuilder setText(String text);

}
