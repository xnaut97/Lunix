package com.github.tezvn.lunix.bukkit.chat.v2.message;

import com.github.tezvn.lunix.bukkit.chat.v2.MessageType;
import com.github.tezvn.lunix.bukkit.chat.v2.Messenger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;
import java.util.function.Predicate;

public class RepeatMessage extends Message {

    private long period;

    private long delay;

    private Predicate<Player> funnction;

    private RepeatMessage(Messenger messenger) {
        super(messenger, MessageType.REPEAT);
    }

    @Override
    public void send() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

            }
        };
    }

    public long getPeriod() {
        return period;
    }

    public RepeatMessage setPeriod(long period) {
        this.period = period;
        return this;
    }

    public long getDelay() {
        return delay;
    }

    public RepeatMessage setDelay(long delay) {
        this.delay = delay;
        return this;
    }

}
