package com.github.tezvn.lunix.chat.v2;

import com.github.tezvn.lunix.Lunix;
import com.github.tezvn.lunix.chat.v2.message.Messenger;
import com.github.tezvn.lunix.chat.v2.mode.CapitalizeMode;
import com.github.tezvn.lunix.chat.v2.message.Message;
import com.github.tezvn.lunix.chat.v2.title.AnimatedTitle;
import com.github.tezvn.lunix.chat.v2.title.Title;
import com.github.tezvn.lunix.chat.v2.title.TitleAnimation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;

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

    public void broadcast(String... messages) {
        broadcast(null, messages);
    }

    public void broadcast(List<UUID> excludes, String... messages) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(excludes != null && excludes.contains(player.getUniqueId()))
                continue;
            sendMessage(player, messages);
        }
    }

    public void sendTitle(Player player, String title) {
        sendTitle(player, title, "");
    }

    public void sendTitle(Player player, String title, String description) {
        player.sendTitle(getChatFormat().color(title), getChatFormat().color(description));
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
            e.printStackTrace();
            return null;
        }
    }

    public Title createTitle() {
        return new Title(this);
    }

    public <T extends AnimatedTitle> T createAnimatedTitle(TitleAnimation animation) {
        return null;
    }

}
