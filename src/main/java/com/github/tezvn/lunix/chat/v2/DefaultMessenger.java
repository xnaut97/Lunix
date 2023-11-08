package com.github.tezvn.lunix.chat.v2;

import com.github.tezvn.lunix.chat.v2.message.MessageType;
import com.github.tezvn.lunix.chat.v2.message.Messenger;
import com.github.tezvn.lunix.chat.v2.mode.CapitalizeMode;
import com.github.tezvn.lunix.chat.v2.message.Message;
import com.github.tezvn.lunix.chat.v2.title.Title;
import com.github.tezvn.lunix.chat.v2.title.animated.AnimatedTitle;
import com.github.tezvn.lunix.chat.v2.title.BaseTitle;
import com.github.tezvn.lunix.chat.v2.title.animated.DefaultAnimatedTitle;
import com.github.tezvn.lunix.chat.v2.title.animated.TitleAnimation;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;

@Getter
public class DefaultMessenger implements Messenger {

    private final Plugin plugin;

    private final ChatFormat chatFormat;

    public DefaultMessenger(Plugin plugin) {
        this.plugin = plugin;
        this.chatFormat = new ChatFormat(this);
    }

    public void sendMessage(CommandSender sender, String... msg) {
        for (String s : msg) {
            sender.sendMessage(getChatFormat().color(s));
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Message> T createMessage(MessageType type) {
        try {
            String name = getChatFormat().capitalize(type.name(), CapitalizeMode.FIRST);
            Class<?> clazz = Class.forName(this.getClass().getPackage().getName() + ".message." + name + "Message");
            Constructor<?> constructor = clazz.getDeclaredConstructor(DefaultMessenger.class);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(this);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Title createTitle() {
        return new BaseTitle(this);
    }

    public AnimatedTitle createAnimatedTitle(TitleAnimation animation) {
        return new DefaultAnimatedTitle(this, animation);
    }

}
