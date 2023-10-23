package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.MessageType;
import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import org.bukkit.scheduler.BukkitRunnable;

public final class TimedMessage extends Message {

    private long times;

    private long period;

    private long delay;

    private TimedMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.TIMED);
    }

    public long getTimes() {
        return times;
    }

    public TimedMessage setTimes(long times) {
        this.times = times;
        return this;
    }

    public long getPeriod() {
        return period;
    }

    public TimedMessage setPeriod(long period) {
        this.period = period;
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
        BukkitRunnable runnable = new BukkitRunnable() {
            int count = 1;
            @Override
            public void run() {
                if(count >= times) {
                    cancel();
                    return;
                }
                getPlayers().forEach(p -> {
                    getMessenger().sendMessage(p, getMessages().toArray(new String[0]));
                    if(getFinishAction() != null)
                        getFinishAction().accept(p);
                });
                count++;
            }
        };
        if(isAsync()) runnable.runTaskTimerAsynchronously(getPlugin(), getDelay(), getPeriod());
        else runnable.runTaskTimer(getPlugin(), getDelay(), getPeriod());
    }
}
