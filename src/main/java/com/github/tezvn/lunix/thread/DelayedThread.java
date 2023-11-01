package com.github.tezvn.lunix.thread;

import org.bukkit.plugin.Plugin;

public abstract class DelayedThread extends AbstractThread {

    private final long ticks;

    public DelayedThread(Plugin plugin, boolean async, long ticks) {
        super(plugin, async, ThreadType.DELAYED);
        this.ticks = ticks;
    }

    @Override
    public final void start() {
        if (isCurrentlyRunning())
            return;
        this.setRunning(true);
        init();
        onStart();
        if (isAsync())
            this.runnable.runTaskLaterAsynchronously(getPlugin(), ticks);
        else
            this.runnable.runTaskLater(getPlugin(), ticks);
    }

}