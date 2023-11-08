package com.github.tezvn.lunix.chat.v2.message;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.github.tezvn.lunix.thread.impl.TimedThread;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
@Accessors(chain = true)
public final class TimedMessage extends Message {

    private long times;

    private long period;

    private long delay;

    @Setter(AccessLevel.PRIVATE)
    private int count;

    @Setter(AccessLevel.PRIVATE)
    private boolean paused;

    private TimedThread thread;

    private TimedMessage(DefaultMessenger messenger) {
        super(messenger, MessageType.TIMED);
    }

    public void start() {
        this.thread = new TimedThread(getPlugin(), isAsync(), getDelay(), getPeriod(), getTimes()) {
            @Override
            public void onRun() {
                if(count >= getTimes()) {
                    thread.stop();
                    return;
                }
                if(isPaused()) return;
                send();
                count++;
            }
        };
        thread.start();
    }

    public void stop() {
        if(this.thread != null) this.thread.stop();
    }

    public void pause() {
        if(this.thread != null) this.thread.pause();
    }

    public void resume() {
        if(this.thread != null) this.thread.resume();
    }

}
