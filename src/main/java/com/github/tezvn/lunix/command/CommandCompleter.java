package com.github.tezvn.lunix.command;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class CommandCompleter {

    private final Map<String, CommandArgument> commands;

    CommandCompleter(AbstractCommand handle) {
        this.commands = handle.getArguments();
    }

    private List<CommandArgument> getCommands(CommandSender sender, String start) {
        return this.commands.values().stream().filter(command -> {
            if(!(command instanceof AutoCompletion)) return false;
            if (!command.getName().startsWith(start)) return false;
            boolean hasPermission = command.getPermission() == null || !command.getPermission().isEmpty();
            return !hasPermission || sender.hasPermission(command.getPermission());
        }).collect(Collectors.toList());
    }

    public @Nonnull List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0)
            return Lists.newArrayList();
        List<CommandArgument> commands = getCommands(sender, args[0]);
        if (!commands.isEmpty()) {
            if (args.length == 1)
                return commands.stream().map(CommandArgument::getName).collect(Collectors.toList());
            else {
                commands.stream().filter(c -> {
                    boolean matchAlias = false;
                    if (!c.getAliases().isEmpty())
                        matchAlias = c.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(args[0]));
                    return c.getName().equalsIgnoreCase(args[0]) || matchAlias;
                }).findAny().ifPresent(c -> ((AutoCompletion) c).tabComplete(sender, args));
            }
        }
        return Lists.newArrayList();
    }
}