package com.github.tezvn.lunix.api.builder;

import com.github.tezvn.lunix.text.GradientText;
import com.github.tezvn.lunix.text.StringBarPercent;

import javax.annotation.Nullable;

public interface GradientTextBuilder extends GradientBuilder<GradientText> {

    @Nullable
    String getText();

    @Deprecated
    GradientTextBuilder setText(String text);


}
