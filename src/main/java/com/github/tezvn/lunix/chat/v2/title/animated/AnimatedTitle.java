package com.github.tezvn.lunix.chat.v2.title.animated;

import com.github.tezvn.lunix.chat.v2.title.Title;

public interface AnimatedTitle extends Title {
    
    TitleAnimation getAnimation();

    int getAnimationTime();

    AnimatedTitle setAnimationTime(int animationTime);

    int getSpacing();

    AnimatedTitle setSpacing(int spacing);
    
}
