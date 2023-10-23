package com.github.tezvn.lunix.transaction;

import org.bukkit.entity.Player;

public abstract class Transaction {

    private final Player leftPlayer;

    private final Player rightPlayer;

    public Transaction(Player leftPlayer, Player rightPlayer) {
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
    }

    public Player getLeftPlayer() {
        return leftPlayer;
    }

    public Player getRightPlayer() {
        return rightPlayer;
    }
}
