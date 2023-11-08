package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;

public class NormalMessage extends Message {

    public NormalMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.NORMAL);
    }

}
