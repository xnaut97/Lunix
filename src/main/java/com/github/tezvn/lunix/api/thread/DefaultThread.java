package com.github.tezvn.lunix.api.thread;

import com.github.tezvn.lunix.thread.ThreadType;
import org.bukkit.plugin.Plugin;

public interface DefaultThread {

    Plugin getPlugin();

    boolean isAsync();

    boolean isRunning();

    ThreadType getThreadType();

    int getId();

}
