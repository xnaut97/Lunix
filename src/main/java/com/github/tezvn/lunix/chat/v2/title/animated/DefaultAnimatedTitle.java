package com.github.tezvn.lunix.chat.v2.title.animated;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.github.tezvn.lunix.chat.v2.title.BaseTitle;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class DefaultAnimatedTitle extends BaseTitle implements AnimatedTitle {

    private final TitleAnimation animation;

    private int animationTime;

    private int spacing;

    public DefaultAnimatedTitle(DefaultMessenger messenger, TitleAnimation animation) {
        super(messenger);
        this.animation = animation;
    }

    public DefaultAnimatedTitle setAnimationTime(int animationTime) {
        this.animationTime = Math.max(1, animationTime);
        return this;
    }

    public DefaultAnimatedTitle setSpacing(int spacing) {
        this.spacing = Math.max(0, spacing);
        return this;
    }

    @Override
    public void send() {
        switch (getAnimation()) {
            case SHRINK -> {
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
            case BOLD -> {
                //        final String a = title.replaceAll("§", "&");
//        final String color = a.substring(0, 2);
//        queue.thenRepeatEvery(40, 3, () -> {
//            TaskQueue q = new TaskQueue();
//
//            for (int i = 0; i < a.length(); i++) {
//                int index = i;
//                q.thenRun(2, () -> {
//                    if (a.charAt(index) == '&') {
//                        return;
//                    }
//
//                    String newString = pdx.mantlecore.java.StringUtils.insert(a, index, "&l");
//                    newString = pdx.mantlecore.java.StringUtils.insert(newString, index + 3, color);
//                    for (Player p : players) {
//                        p.sendTitle(new Title(newString.replaceAll("&", "§"), subtitle, 0, 5 * 20, 30));
//                    }
//                });
//            }
//
//            q.execute(PDXMantleCore.getInstance());
//        });
//        queue.execute(PDXMantleCore.getInstance());

                new BukkitRunnable() {

                    int times = getMessenger().getChatFormat().stripColor(getTitle()).length();
                    int count = 0;
                    @Override
                    public void run() {
                        if(count == times) {
                            return;
                        }

                        count++;
                    }
                }.runTaskTimerAsynchronously(getMessenger().getPlugin(), 0, 5);
            }
        }
    }
}
