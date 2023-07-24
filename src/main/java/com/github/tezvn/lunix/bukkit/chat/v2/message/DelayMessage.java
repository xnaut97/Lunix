package com.github.tezvn.lunix.bukkit.chat.v2.message;

import com.github.tezvn.lunix.bukkit.chat.v2.MessageType;
import com.github.tezvn.lunix.bukkit.chat.v2.Messenger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public final class DelayMessage extends Message {

    private long delay;

    private DelayMessage(Messenger messenger) {
        super(messenger, MessageType.DELAY);
    }

    public long getDelay() {
        return delay;
    }

    public DelayMessage setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    @Override
    public void send() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().forEach(p -> getMessenger().sendMessage(p, getMessages().toArray(new String[0])));
                if(getFinishAction() != null)
                    getPlayers().forEach(p -> getFinishAction().accept(p));
            }
        };
        if(isAsync()) runnable.runTaskLaterAsynchronously(getPlugin(), delay);
        else runnable.runTaskLater(getPlugin(), delay);
    }
}
