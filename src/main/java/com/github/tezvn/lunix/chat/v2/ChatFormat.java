package com.github.tezvn.lunix.chat.v2;

import com.github.tezvn.lunix.chat.v2.mode.CapitalizeMode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatFormat {

    private final DefaultMessenger messenger;

    public ChatFormat(DefaultMessenger messenger) {
        this.messenger = messenger;
    }

    public DefaultMessenger getMessenger() {
        return messenger;
    }

    public String capitalize(String str, CapitalizeMode mode) {
        String[] split = str.split("[\\s!@#$%^&*()=+-_,]+");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            switch (mode) {
                case FIRST:
                    sb.append(s.substring(0, 1).toUpperCase());
                    if (s.length() > 1)
                        sb.append(s.substring(1).toLowerCase());
                    break;
                case ALL:
                    sb.append(s.toUpperCase());
                    break;
            }
        }
        return sb.toString();
    }

    public String format(Location location) {
        return format(location, true);
    }

    public String format(Location location, boolean includeBrackets) {
        return (includeBrackets ? "[" : "")
                + (location.getWorld() == null ? "none" : location.getWorld().getName()) + ", " +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ() + (includeBrackets ? "]" : "");
    }

    public String format(Material material) {
        StringBuilder sb = new StringBuilder();
        String[] name = material.name().split("_");
        for (String s : name) {
            sb.append(capitalize(s, CapitalizeMode.FIRST));
        }
        return sb.toString();
    }

    public boolean hasSpecialCharacters(String str) {
        return getSpecialCharacters(str).size() > 0;
    }

    public List<String> getSpecialCharacters(String str) {
        return Arrays.stream(str.split(""))
                .filter(s -> !s.matches("[a-zA-Z0-9]*"))
                .collect(Collectors.toList());
    }

    public String stripSpecialCharacters(String str) {
        return Arrays.stream(str.split(""))
                .filter(s -> s.matches("[a-zA-Z0-9]*"))
                .collect(Collectors.joining());
    }

    public String color(String str) {
        return str.replace("&", "§");
    }

    public List<String> color(String... msg) {
        return Arrays.stream(msg).map(this::color).collect(Collectors.toList());
    }

    public List<String> color(List<String> list) {
        return color(list.toArray(new String[0]));
    }

    public String stripColor(String title) {
        return ChatColor.stripColor(title);
    }
}
