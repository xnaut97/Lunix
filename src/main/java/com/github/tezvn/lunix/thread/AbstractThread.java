package com.github.tezvn.lunix.thread;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class AbstractThread {

    private final Plugin plugin;
    private final boolean async;
    protected BukkitRunnable runnable;
    protected ThreadType type;
    protected int id;
    private boolean running;

    public AbstractThread(Plugin plugin, boolean async, ThreadType type) {
        this.plugin = plugin;
        this.async = async;
        this.type = type;
    }

    public abstract void onTick();

    public void onStop() {
    }

    public void onStart() {
    }

    protected Plugin getPlugin() {
        return this.plugin;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    protected boolean isCurrentlyRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(getId());
    }

    public abstract void start();

    public void stop() {
        if (!isRunning())
            return;
        setRunning(false);
    }

    protected void init() {
        if (isCurrentlyRunning())
            return;
        this.setRunning(true);
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    cancel();
                    onStop();
                    return;
                }
                onTick();
            }
        };
    }

}
