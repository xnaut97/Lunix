package com.github.tezvn.lunix.bossbar;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ProgressBar {

    @Getter(AccessLevel.PRIVATE)
    private final KeyedBossBar bossBar;

    @Getter
    private boolean deleted;

    ProgressBar(ProgressBarBuilder builder) {
        this.bossBar = Bukkit.createBossBar(new NamespacedKey("progress-bar", builder.getKey()),
                builder.getTitle(), builder.getColor(), builder.getStyle(), builder.getFlags().toArray(new BarFlag[0]));
        this.bossBar.setProgress(builder.getProgress());
        this.bossBar.setVisible(true);
    }

    public double getProgress() {
        return this.bossBar.getProgress();
    }

    public boolean isVisible() {
        return this.bossBar.isVisible();
    }

    public boolean isVisible(Player player) {
        if(!this.isVisible()) return false;

        return this.bossBar.getPlayers().contains(player);
    }

    public ProgressBar setTitle(String title) {
        if(isDeleted()) return this;

        this.bossBar.setTitle(title.replace("&", "§"));
        return this;
    }

    public ProgressBar setColor(BarColor color) {
        if(isDeleted()) return this;

        this.bossBar.setColor(color);
        return this;
    }

    public ProgressBar setStyle(BarStyle style) {
        if(isDeleted()) return this;

        this.bossBar.setStyle(style);
        return this;
    }

    public ProgressBar addFlags(BarFlag... flags) {
        if(isDeleted()) return this;

        Arrays.stream(flags).forEach(flag -> {
            if (!this.bossBar.hasFlag(flag)) this.bossBar.addFlag(flag);
        });
        return this;
    }

    public ProgressBar removeFlags(BarFlag... flags) {
        if(isDeleted()) return this;

        Arrays.stream(flags).forEach(flag -> {
            if (this.bossBar.hasFlag(flag)) this.bossBar.removeFlag(flag);
        });
        return this;
    }

    public ProgressBar showPlayer(Player player) {
        if(isDeleted()) return this;

        if (!this.bossBar.getPlayers().contains(player)) this.bossBar.addPlayer(player);
        return this;
    }

    public ProgressBar hidePlayer(Player player) {
        if(isDeleted()) return this;

        if (this.bossBar.getPlayers().contains(player)) this.bossBar.removePlayer(player);
        return this;
    }

    public ProgressBar clearPlayer() {
        if(isDeleted()) return this;

        this.bossBar.removeAll();
        return this;
    }

    public ProgressBar setVisible(boolean visible) {
        if(isDeleted()) return this;

        this.bossBar.setVisible(visible);
        return this;
    }

    public ProgressBar setProgress(double progress) {
        this.bossBar.setProgress(Math.max(0, Math.min(1, progress)));
        return this;
    }

    public ProgressBar addProgress(double percent) {
        return setProgress(getProgress() + (Math.max(0, Math.min(100, percent)) / 100));
    }

    public ProgressBar removeProgress(double percent) {
        return setProgress(getProgress() - (Math.max(0, Math.min(100, percent)) / 100));
    }

    public ProgressBar clearProgress() {
        return setProgress(0);
    }

    public ProgressBar maxProgress() {
        return setProgress(1);
    }

    public ProgressBar complete(Runnable action) {
        maxProgress();
        if(action != null) action.run();
        return this;
    }

    public void delete() {
        if(isDeleted()) return;
        this.bossBar.removeAll();
        this.bossBar.setVisible(false);
        Bukkit.removeBossBar(this.bossBar.getKey());
        deleted = true;
    }

}
