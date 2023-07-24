package com.github.tezvn.lunix.bukkit.chat.v2.title;

import com.github.tezvn.lunix.bukkit.chat.v2.ChatFormat;
import com.github.tezvn.lunix.bukkit.chat.v2.Messenger;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class Title {

    private final Messenger messenger;

    private String title = "";

    private String description = "";

    private int fadeIn = 10;

    private int stay = 70;

    private int fadeOut = 20;

    private final Set<Player> players = Sets.newHashSet();

    public Title(Messenger messenger) {
        this.messenger = messenger;
    }

    public String getTitle() {
        return title;
    }

    public Title setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Title setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public int getStay() {
        return stay;
    }

    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Title addPlayer(Player... players) {
        this.players.addAll(Arrays.asList(players));
        return this;
    }

    public void send() {
        ChatFormat chatFormat = messenger.getChatFormat();
        getPlayers().stream().filter(OfflinePlayer::isOnline).forEach(p ->
                p.sendTitle(chatFormat.color(getTitle()), chatFormat.color(getDescription()),
                        getFadeIn(), getStay(), getFadeOut()));
    }

}