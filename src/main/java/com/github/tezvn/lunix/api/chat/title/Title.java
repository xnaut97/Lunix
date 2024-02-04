package com.github.tezvn.lunix.api.chat.title;

import org.bukkit.entity.Player;

public interface Title {

    String getTitle();

    Title setTitle(String title);

    String getDescription();

    Title setDescription(String description);

    Title setFadeIn(int fadeIn);

    Title setStay(int stay);

    Title setFadeOut(int fadeOut);

    Title addPlayer(Player... players);

    void send();
    
}
