package com.github.tezvn.lunix.bukkit.chat.v2.title;

import com.github.tezvn.lunix.bukkit.chat.v2.Messenger;

public abstract class AnimatedTitle extends Title {

    private final TitleAnimation animation;

    private int animationTime;

    private int spacing;

    public AnimatedTitle(Messenger messenger, TitleAnimation animation) {
        super(messenger);
        this.animation = animation;
    }

    public TitleAnimation getAnimation() {
        return animation;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public AnimatedTitle setAnimationTime(int animationTime) {
        this.animationTime = Math.max(1, animationTime);
        return this;
    }

    public int getSpacing() {
        return spacing;
    }

    public AnimatedTitle setSpacing(int spacing) {
        this.spacing = Math.max(0, spacing);
        return this;
    }
}
