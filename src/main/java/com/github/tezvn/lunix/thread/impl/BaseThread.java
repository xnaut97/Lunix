package com.github.tezvn.lunix.thread.impl;

import com.github.tezvn.lunix.thread.ThreadType;
import org.bukkit.plugin.Plugin;

public abstract class BaseThread extends AbstractThread {

    public BaseThread(Plugin plugin, boolean async) {
        super(plugin, async, ThreadType.BASE);
    }

    @Override
    public final void start() {
        init();
        onStart();
        if (!isAsync())
            this.runnable.runTask(getPlugin());
        else
            this.runnable.runTaskAsynchronously(getPlugin());
    }

}