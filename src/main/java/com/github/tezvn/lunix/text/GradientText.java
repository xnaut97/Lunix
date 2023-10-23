package com.github.tezvn.lunix.text;

import com.github.tezvn.lunix.builder.GradientTextBuilder;
import com.github.tezvn.lunix.color.*;
import jline.internal.Preconditions;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class GradientText {

    private final String text;

    private final ColorSet leftColor;

    private final ColorSet rightColor;

    GradientText(GradientTextBuilder builder) {
        if(builder.getText() == null) throw new NullPointerException("Text must not be null");
        this.text = builder.getText();
        this.leftColor = convert(builder.getLeftColor());
        this.rightColor = convert(builder.getRightColor());
    }

    private String build() {
        String[] split = text.split("");
        List<String> hexList = toHexList();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            String color = hexList.get(i);
            sb.append(color.replace("&", "§").replace("#", "§x"))
                    .append("");
        }
        return sb.toString();
    }

    public static GradientTextBuilder builder() {
        return new GradientTextBuilderImpl();
    }

    private List<String> toHexList() {
        List<String> converted = new ArrayList<>();
        int distanceRed = leftColor.getRed() < rightColor.getRed()
                ? rightColor.getRed() - leftColor.getRed()
                : (leftColor.getRed() - rightColor.getRed()) / text.length();
        int distanceBlue = leftColor.getBlue() < rightColor.getBlue()
                ? rightColor.getBlue() - leftColor.getBlue()
                : (leftColor.getBlue() - rightColor.getBlue()) / text.length();
        int distanceGreen = leftColor.getGreen() < rightColor.getGreen()
                ? rightColor.getGreen() - leftColor.getGreen()
                : (leftColor.getGreen() - rightColor.getGreen()) / text.length();
        for (int i = 0; i < text.length(); i++) {
            leftColor.setRed(leftColor.getRed() <= rightColor.getRed()
                            ? leftColor.getRed() + distanceRed
                            : leftColor.getRed() - distanceRed)
                    // Green
                    .setGreen(leftColor.getGreen() <= rightColor.getGreen()
                            ? leftColor.getGreen() + distanceGreen
                            : leftColor.getGreen() - distanceGreen)
                    // Blue
                    .setBlue(leftColor.getBlue() <= rightColor.getBlue()
                            ? leftColor.getBlue() + distanceBlue
                            : leftColor.getBlue() - distanceBlue);
            String hex = String.format("#%02x%02x%02x", leftColor.getRed(), leftColor.getGreen(), leftColor.getBlue());
            converted.add(hex);
        }
        return converted;
    }

    private ColorSet convert(String color) {
        if(color == null) throw new NullPointerException("Color must not be null");
        return color.startsWith("#") ? ColorSet.fromHex(color) : ColorContainer.getColor(color);
    }

    private static class GradientTextBuilderImpl extends GradientBuilderImpl<GradientText> implements GradientTextBuilder {

        private String text;

        @Override
        public GradientText build() {
            return new GradientText(this);
        }

        public String getText() {
            return text;
        }

        @Override
        public GradientTextBuilder setText(String text) {
            this.text = text;
            return this;
        }
    }

}
