package com.github.tezvn.lunix.thread.impl;

import com.github.tezvn.lunix.thread.DefaultThread;
import com.github.tezvn.lunix.thread.ThreadType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class AbstractThread implements DefaultThread {

    private final Plugin plugin;
    private final boolean async;
    protected BukkitRunnable runnable;
    @Setter(AccessLevel.MODULE)
    protected ThreadType threadType;
    protected int id;

    public AbstractThread(Plugin plugin, boolean async, ThreadType type) {
        this.plugin = plugin;
        this.async = async;
        this.threadType = type;
    }

    public abstract void onRun();

    public abstract void start();

    public void onStop() {
    }

    public void onStart() {
    }

    public boolean isRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(getId());
    }

    public void stop() {
        if (this.runnable != null && !this.runnable.isCancelled())
            this.runnable.cancel();
    }

    protected void init() {
        if (isRunning())
            return;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    cancel();
                    stop();
                    return;
                }
                onRun();
            }
        };
    }

}
