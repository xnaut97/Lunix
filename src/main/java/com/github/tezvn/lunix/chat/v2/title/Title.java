package com.github.tezvn.lunix.chat.v2.title;

import org.bukkit.entity.Player;

public interface Title {

    String getTitle();

    BaseTitle setTitle(String title);

    String getDescription();

    BaseTitle setDescription(String description);

    BaseTitle setFadeIn(int fadeIn);

    BaseTitle setStay(int stay);

    BaseTitle setFadeOut(int fadeOut);

    BaseTitle addPlayer(Player... players);

    void send();
    
}
