package com.github.tezvn.lunix.menu.slot;

import java.util.Arrays;

public class Boxed extends SlotBuilder {

    public Boxed() {
        Arrays.stream(Position.values()).forEach(p -> setBorder(p, 1));
    }
}