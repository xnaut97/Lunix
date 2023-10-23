package com.github.tezvn.lunix.color;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public abstract class TextFormat {

    private final Set<String> formats = Sets.newHashSet();

    public Set<String> getFormats() {
        return formats;
    }

    public TextFormat addFormat(String... formats) {
        this.formats.addAll(Arrays.asList(formats));
        return this;
    }

}
