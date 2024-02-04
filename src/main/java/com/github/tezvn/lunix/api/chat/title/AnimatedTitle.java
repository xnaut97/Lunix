package com.github.tezvn.lunix.api.chat.title;

import com.github.tezvn.lunix.chat.v2.title.animated.TitleAnimation;

public interface AnimatedTitle extends Title {
    
    TitleAnimation getAnimation();

    int getAnimationTime();

    AnimatedTitle setAnimationTime(int animationTime);

    int getSpacing();

    AnimatedTitle setSpacing(int spacing);
    
}
