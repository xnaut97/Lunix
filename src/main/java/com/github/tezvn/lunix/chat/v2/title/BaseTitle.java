package com.github.tezvn.lunix.chat.v2.title;

import com.github.tezvn.lunix.chat.v2.ChatFormat;
import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

@Getter
public class BaseTitle implements Title {

    private final DefaultMessenger messenger;

    private String title = "";

    private String description = "";

    private int fadeIn = 10;

    private int stay = 70;

    private int fadeOut = 20;

    private final Set<Player> players = Sets.newHashSet();

    public BaseTitle(DefaultMessenger messenger) {
        this.messenger = messenger;
    }

    public String getTitle() {
        return messenger.getChatFormat().color(this.title);
    }

    public BaseTitle setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return messenger.getChatFormat().color(this.description);
    }

    public BaseTitle setDescription(String description) {
        this.description = description;
        return this;
    }

    public BaseTitle setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public BaseTitle setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public BaseTitle setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public BaseTitle addPlayer(Player... players) {
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
