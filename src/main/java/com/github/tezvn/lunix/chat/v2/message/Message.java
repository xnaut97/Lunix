package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.MessageType;
import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public abstract class Message {

    private final DefaultMessenger messenger;

    private final List<String> messages = Lists.newArrayList();

    private final Set<Player> players = Sets.newHashSet();

    private final MessageType type;

    private Consumer<Player> finishAction;

    private boolean async;

    public Message(DefaultMessenger messenger, MessageType type) {
        this.messenger = messenger;
        this.type = type;
    }

    public Plugin getPlugin() {
        return messenger.getPlugin();
    }

    public Message addMessage(String... strings) {
        this.messages.addAll(Arrays.asList(strings));
        return this;
    }

    public Set<Player> getPlayers() {
        return players.stream().filter(OfflinePlayer::isOnline).collect(Collectors.toSet());
    }

    public Message addPlayer(Player... players) {
        this.players.addAll(Arrays.asList(players));
        return this;
    }

    public Message removePlayer(Player player) {
        this.players.removeIf(p -> p.getUniqueId().equals(player.getUniqueId()));
        return this;
    }

    public MessageType getType() {
        return type;
    }

    protected Consumer<Player> getFinishAction() {
        return finishAction;
    }

    public Message onFinish(Consumer<Player> finishAction) {
        this.finishAction = finishAction;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public Message setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public abstract void send();

}