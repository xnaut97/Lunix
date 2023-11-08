package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class Message {

    private final DefaultMessenger messenger;

    private final List<String> messages = Lists.newArrayList();

    private final Set<Player> players = Sets.newHashSet();

    private final MessageType type;

    @Getter(AccessLevel.PRIVATE)
    private Consumer<Player> successAction;

    @Getter(AccessLevel.PRIVATE)
    private Consumer<Player> failedAction;

    @Getter(AccessLevel.PRIVATE)
    private Predicate<Player> condition;

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

    protected Consumer<Player> getSuccessAction() {
        return successAction;
    }

    public Message onFinish(Consumer<Player> finishAction) {
        this.successAction = finishAction;
        return this;
    }

    public Message setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public Message onSending(Predicate<Player> condition) {
        this.condition = condition;
        return this;
    }

    public void send() {
        send0();
    }

    protected void send0() {
        getPlayers().forEach(player -> {
            if(condition != null && !condition.test(player)) {
                if(getFailedAction() != null) getFailedAction().accept(player);
                return;
            }
            getMessenger().sendMessage(player, getMessages().toArray(new String[0]));
            if(getSuccessAction() != null) getSuccessAction().accept(player);
        });
    }

}
