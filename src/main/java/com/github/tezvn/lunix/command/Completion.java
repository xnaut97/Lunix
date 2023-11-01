package com.github.tezvn.lunix.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Completion {

    List<String> tabComplete(CommandSender sender, String[] args);

}
