package com.github.tezvn.lunix.api.command;

import org.bukkit.command.CommandSender;

public interface ConsoleExecutor {

    void consoleExecute(CommandSender sender, String[] args);

}
