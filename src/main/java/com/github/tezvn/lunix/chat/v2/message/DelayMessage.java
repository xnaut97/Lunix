package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.api.builder.GradientBuilder;
import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public final class DelayMessage extends Message {

    private long delay;

    private DelayMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.DELAY);
    }

    public DelayMessage setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    @Override
    public void send() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                send0();
            }
        };
        if(isAsync()) runnable.runTaskLaterAsynchronously(getPlugin(), delay);
        else runnable.runTaskLater(getPlugin(), delay);
    }
}
