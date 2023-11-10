package com.github.tezvn.lunix.bossbar;

import com.github.tezvn.lunix.java.RandomID;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter(AccessLevel.PROTECTED)
@Accessors(chain = true)
public class ProgressBossBarBuilder {

    private String title;

    private BarColor color;

    private BarStyle style;

    @Setter(AccessLevel.NONE)
    private List<BarFlag> flags = Lists.newArrayList();

    @Setter(AccessLevel.NONE)
    private List<Player> players = Lists.newArrayList();

    @Setter(AccessLevel.NONE)
    private double progress = 0;

    private String key = RandomID.of(4).generate();

    public ProgressBossBarBuilder() {
    }

    public ProgressBossBarBuilder addFlags(BarFlag... flags) {
        Arrays.stream(flags).forEach(flag -> {
            if(this.flags.contains(flag)) return;
            this.flags.add(flag);
        });
        return this;
    }

    public ProgressBossBarBuilder addPlayers(Player... players) {
        Arrays.stream(players).forEach(player -> {
            if(this.players.contains(player)) return;
            this.players.add(player);
        });
        return this;
    }

    public ProgressBossBarBuilder setProgress(double progress) {
        this.progress = Math.max(0, Math.min(1, progress));
        return this;
    }

    public ProgressBossBar build() {
        return new ProgressBossBar(this);
    }

}
