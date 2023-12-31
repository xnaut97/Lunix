package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.github.tezvn.lunix.thread.impl.TimerThread;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@Getter
@Setter
@Accessors(chain = true)
public class TimerMessage extends Message {

    private long period;

    private long delay;

    private Predicate<Player> condition;

    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PROTECTED)
    private TimerThread timerThread;

    private TimerMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.TIMER);
    }

    public void send() {
        if (this.timerThread == null)
            this.timerThread = new TimerThread(getMessenger().getPlugin(), isAsync(), getDelay(), getPeriod()) {
                @Override
                public void onRun() {
                    send0();
                }
            };
        if (!this.timerThread.isRunning())
            this.timerThread.start();
    }

    public void stop() {
        if (this.timerThread != null) timerThread.stop();
    }

    public void pause() {
        if (this.timerThread != null) timerThread.pause();
    }

}
