package com.github.tezvn.lunix.api.chat;

import com.github.tezvn.lunix.chat.v2.ChatFormat;
import com.github.tezvn.lunix.chat.v2.message.Message;
import com.github.tezvn.lunix.chat.v2.message.MessageType;
import com.github.tezvn.lunix.api.chat.title.Title;
import com.github.tezvn.lunix.api.chat.title.AnimatedTitle;
import com.github.tezvn.lunix.chat.v2.title.animated.TitleAnimation;

public interface Messenger {

    <T extends Message> T createMessage(MessageType type);

    Title createTitle();

    AnimatedTitle createAnimatedTitle(TitleAnimation animation);

    ChatFormat getChatFormat();

}
