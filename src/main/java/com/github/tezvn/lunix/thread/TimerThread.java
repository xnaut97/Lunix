package com.github.tezvn.lunix.thread;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TimerThread extends AbstractThread {

    private final long ticks;

    private final long delay;

    private boolean pause;

    private long count = 0;

    public TimerThread(Plugin plugin, boolean async, long delay, long ticks) {
        super(plugin, async, ThreadType.TIMER);
        this.delay = delay;
        this.ticks = ticks;
    }

    @Override
    protected void init() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    cancel();
                    onStop();
                    return;
                }
                if (isPaused())
                    return;
                onTick();
                count++;
                if (count >= Long.MAX_VALUE)
                    count = 0;
            }
        };
    }

    @Override
    public final void start() {
        if (isCurrentlyRunning())
            return;
        this.setRunning(true);
        init();
        onStart();
        if (isAsync())
            this.runnable.runTaskTimerAsynchronously(getPlugin(), delay, ticks);
        else
            this.runnable.runTaskTimer(getPlugin(), delay, ticks);
    }

    public long getTicks() {
        return ticks;
    }

    public long getDelay() {
        return delay;
    }

    public boolean isPaused() {
        return pause;
    }

    public long getCount() {
        return count;
    }

    public void pause() {
        this.pause = true;
        onPause();
    }

    public void resume() {
        this.pause = false;
        onResume();
    }

    public void onPause() {
    }

    public void onResume() {
    }
}