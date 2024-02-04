package com.github.tezvn.lunix.color;

import com.github.tezvn.lunix.api.builder.GradientBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public abstract class GradientBuilderImpl<T> implements GradientBuilder<T> {

    private String leftColor;

    private String rightColor;

    @Override
    public String getRightColor() {
        return rightColor;
    }

    @Override
    public GradientBuilder<T> setLeftColor(String hex) {
        validateHex(hex);
        this.leftColor = hex;
        return this;
    }

    @Override
    public GradientBuilder<T> setLeftColor(ChatColor color) {
        validateColor(color);
        this.leftColor = color.toString();
        return this;
    }

    @Override
    public GradientBuilder<T> setRightColor(String hex) {
        validateHex(hex);
        this.rightColor = hex;
        return this;
    }

    @Override
    public GradientBuilder<T> setRightColor(ChatColor color) {
        validateColor(color);
        this.rightColor = color.toString();
        return this;
    }

    private void validateHex(String str) {
        if(str.startsWith("#") && str.length() == 7) return;
        throw new IllegalArgumentException("Invalid hex color " + str);
    }

    private void validateColor(ChatColor color) {
        if(color.isColor()) return;
        throw new IllegalArgumentException("Invalid chat color " + color);
    }

}
