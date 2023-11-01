package com.github.tezvn.lunix.menu.slot;

import java.util.Arrays;

public class Crafting extends SlotBuilder {

    private int width;
    private int height;
    private int resultSlot;

    public Crafting() {
        this(3, 3);
    }

    public Crafting(int width, int height) {
        setWidth(width);
        setHeight(height);
        Arrays.stream(Position.values()).forEach(p -> {
            int value = 0;
            switch (p) {
                case LEFT:
                case TOP:
                    value = 1;
                    break;
                case RIGHT:
                    value = 6;
                    break;
                case BOTTOM:
                    value = 3;
                    break;
            }
            setBorder(p, value);
        });
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.min(6, Math.max(2, width));
        setBorder(Position.RIGHT, getBorder(Position.RIGHT) + 2 - getWidth());
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.min(3, Math.max(2, height));
    }

    public int getResultSlot() {
        return resultSlot;
    }

    public void setResultSlot(int resultSlot) {
        this.resultSlot = Math.max(0, Math.min(resultSlot, 53));
    }

}