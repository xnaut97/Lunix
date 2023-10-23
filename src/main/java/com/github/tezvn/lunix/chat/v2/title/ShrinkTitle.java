package com.github.tezvn.lunix.chat.v2.title;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class ShrinkTitle extends AnimatedTitle {
    public ShrinkTitle(DefaultMessenger messenger) {
        super(messenger, TitleAnimation.SHRINK);
    }

    @Override
    public void send() {
        new BukkitRunnable() {
            int spacing = getSpacing();
            @Override
            public void run() {
                if(spacing == -1) {
                    cancel();
                    return;
                }
                String[] letters = getTitle().split("");
                StringBuilder builder = new StringBuilder();
                if (spacing > 0) {
                    for (int i = 0; i < letters.length; i++) {
                        String character = letters[i];
                        builder.append(character);
                        if (i != letters.length - 1)
                            builder.append(StringUtils.repeat(" ", spacing));
                    }
                } else
                    builder.append(getTitle());
                getPlayers().forEach(p -> p.sendTitle(spacing > 0 ? builder.toString() : getTitle(),
                        getDescription(), 0, 5 * 20, 30));
                spacing--;
            }
        }.runTaskTimerAsynchronously(getMessenger().getPlugin(), 0, 1);
    }
}
