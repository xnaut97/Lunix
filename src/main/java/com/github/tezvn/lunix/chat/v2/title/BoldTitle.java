package com.github.tezvn.lunix.chat.v2.title;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import org.bukkit.scheduler.BukkitRunnable;

public class BoldTitle extends AnimatedTitle {
    BoldTitle(DefaultMessenger messenger, TitleAnimation animation) {
        super(messenger, animation);
    }

    @Override
    public void send() {
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
