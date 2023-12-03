package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.ChatFormat;
import com.github.tezvn.lunix.chat.v2.title.Title;
import com.github.tezvn.lunix.chat.v2.title.animated.AnimatedTitle;
import com.github.tezvn.lunix.chat.v2.title.animated.TitleAnimation;

public interface Messenger {

    <T extends Message> T createMessage(MessageType type);

    Title createTitle();

    AnimatedTitle createAnimatedTitle(TitleAnimation animation);

    ChatFormat getChatFormat();

}
