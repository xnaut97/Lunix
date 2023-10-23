package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.MessageType;
import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.function.Predicate;

@Getter
public class RepeatMessage extends Message {

    private long period;

    private long delay;

    private Predicate<Player> condition;

    private RepeatMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.REPEAT);
    }

    public RepeatMessage setPeriod(long period) {
        this.period = period;
        return this;
    }

    public long getDelay() {
        return delay;
    }

    public RepeatMessage setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public RepeatMessage onCondition(Predicate<Player> condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public void send() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(getPlayers().isEmpty()) {
                    cancel();
                    return;
                }
                Iterator<Player> iterator = getPlayers().iterator();
                while(iterator.hasNext()) {
                    Player player = iterator.next();
                    if(condition != null && condition.test(player)) {
                        iterator.remove();
                        return;
                    }
                    getMessenger().sendMessage(player, getMessages().toArray(new String[0]));
                    if(getFinishAction() != null)
                        getFinishAction().accept(player);
                }
            }
        };
        if(isAsync()) runnable.runTaskTimerAsynchronously(getPlugin(), getDelay(), getPeriod());
        else runnable.runTaskTimer(getPlugin(), getDelay(), getPeriod());
    }
}
