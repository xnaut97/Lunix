package com.github.tezvn.lunix.thread;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class TimedThread extends TimerThread {

    private final long times;

    private int currentTimes;

    public TimedThread(Plugin plugin, boolean async, long delay, long ticks, long times) {
        super(plugin, async, delay, ticks);
        this.times = times;
        this.type = ThreadType.TIMED;
    }

    public void onSuccess() {

    }

    @Override
    protected void init() {
        this.runnable = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (!isRunning()) {
                    cancel();
                    onStop();
                    return;
                }
                if (count >= getTimes()) {
                    cancel();
                    onSuccess();
                    return;
                }
                if (isPaused())
                    return;
                onTick();
                currentTimes = count++;
            }
        };
    }
}