package com.github.tezvn.lunix.thread;

import org.bukkit.plugin.Plugin;

public interface DefaultThread {

    Plugin getPlugin();

    boolean isAsync();

    boolean isRunning();

    ThreadType getThreadType();

    int getId();

}
