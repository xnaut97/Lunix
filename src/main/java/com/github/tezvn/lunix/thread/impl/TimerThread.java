package com.github.tezvn.lunix.thread.impl;

import com.github.tezvn.lunix.thread.ThreadType;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class TimerThread extends AbstractThread {

    private final long ticks;

    private final long delay;

    private boolean paused;

    private long count = 0;

    public TimerThread(Plugin plugin, boolean async, long delay, long ticks) {
        super(plugin, async, ThreadType.TIMER);
        this.delay = delay;
        this.ticks = ticks;
    }

    @Override
    public final void start() {
        init();
        if (isAsync())
            this.runnable.runTaskTimerAsynchronously(getPlugin(), delay, ticks);
        else
            this.runnable.runTaskTimer(getPlugin(), delay, ticks);
        onStart();
    }

    @Override
    protected void init() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (isPaused()) return;
                onRun();
                count++;
            }
        };
    }

    public void pause() {
        this.paused = true;
        onPause();
    }

    public void resume() {
        this.paused = false;
        onResume();
    }

    public void onPause() {
    }

    public void onResume() {
    }

}