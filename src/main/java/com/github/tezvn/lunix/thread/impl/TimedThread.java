package com.github.tezvn.lunix.thread.impl;

import com.github.tezvn.lunix.thread.ThreadType;
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
        setThreadType(ThreadType.TIMED);
    }

    public void onSuccess() {

    }

    @Override
    protected void init() {
        this.runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (currentTimes >= getTimes()) {
                    cancel();
                    onSuccess();
                    return;
                }
                if (isPaused()) return;
                onRun();
                currentTimes++;
            }
        };
    }
}