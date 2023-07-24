package com.github.tezvn.lunix.bukkit.chat.v2.message;

import com.github.tezvn.lunix.bukkit.chat.v2.MessageType;
import com.github.tezvn.lunix.bukkit.chat.v2.Messenger;

public final class TimedMessage extends Message {

    private long times;

    private long delay;

    private TimedMessage(Messenger messenger) {
        super(messenger, MessageType.TIMED);
    }

    public long getTimes() {
        return times;
    }

    public TimedMessage setTimes(long times) {
        this.times = times;
        return this;
    }

    public long getDelay() {
        return delay;
    }

    public TimedMessage setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    @Override
    public void send() {

    }
}
