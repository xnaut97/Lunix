package com.github.tezvn.lunix.text;

import com.github.tezvn.lunix.api.builder.ProgressStringBarBuilder;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class ProgressStringBar {

    private static final Map<UUID, Listener> listeners = Maps.newHashMap();

    private final String progressBar;

    ProgressStringBar(ProgressStringBarBuilderImpl builder) {
        float percent = (float) (builder.current / builder.total);

        int progressBars = (int) (builder.quantity * percent);
        String currentColor = builder.completeColor.getColorOf((int) (percent * 100), "§c");
        this.progressBar = Strings.repeat(currentColor + builder.completedSymbol, progressBars)
                + Strings.repeat(builder.incompleteColor.replaceAll("&", "§") + builder.incompleteSymbol, builder.quantity - progressBars)
                + (builder.displayPercent ? " &7[" + currentColor + (percent * 100) + "%&7]" : "");
    }

    public String get() {
        return this.progressBar;
    }

    /**
     * Display progress bar on top of entity
     * @param entity Entity to display
     */
    public void display(LivingEntity entity) {
        entity.setCustomName(get());
        entity.setCustomNameVisible(true);
    }

    /**
     * Display progress bar in specific location with given cooldown
     * @param location Location to display
     * @param plugin Plugin to handle
     * @param cooldown Minecraft ticks
     */
    public void display(Location location, @Nonnull Plugin plugin, int cooldown) {
        ArmorStand as = spawnArmorStand(location);
        if(as == null)
            return;
        registerListener(as, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!as.isDead())
                    as.remove();
                unregisterListener(as);
            }
        }.runTaskLater(plugin, cooldown);
    }

    /**
     * Display progress bar in specific location with given stop function
     * @param location Location to display
     * @param plugin Plugin to handle
     * @param function
     */
    public void display(Location location, Plugin plugin, @Nonnull Predicate<ArmorStand> function) {
        Preconditions.checkNotNull(function, "function cannot be null");
        ArmorStand as = spawnArmorStand(location);
        if(as == null)
            return;
        registerListener(as, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(function.test(as)) {
                    if(!as.isDead())
                        as.remove();
                    unregisterListener(as);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    private ArmorStand spawnArmorStand(Location location) {
        if(location.getWorld() == null)
            return null;
        ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);
        display(as);
        return as;
    }

    private void registerListener(ArmorStand as, Plugin plugin) {
        listeners.computeIfAbsent(as.getUniqueId(), uuid -> {
            Listener listener = new Listener() {
                @EventHandler
                public void onStandInteraction(PlayerArmorStandManipulateEvent event) {
                    ArmorStand stand = event.getRightClicked();
                    if (stand.getUniqueId().equals(as.getUniqueId()))
                        event.setCancelled(true);
                }

                @EventHandler
                public void onEntityDeath(EntityDeathEvent event) {

                }
            };
            Bukkit.getPluginManager().registerEvents(listener, plugin);
            return listener;
        });
    }

    private void unregisterListener(ArmorStand as) {
        Listener listener = listeners.remove(as.getUniqueId());
        if(listener != null)
            HandlerList.unregisterAll(listener);
    }

    /**
     * Create new builder
     */
    public static ProgressStringBarBuilder builder() {
        return new ProgressStringBarBuilderImpl();
    }

    private static class ProgressStringBarBuilderImpl implements ProgressStringBarBuilder {

        private double current;

        private double total;

        private int quantity;

        private String completedSymbol = "❙";

        private String incompleteSymbol = "";

        private ColorMap completeColor;

        private String incompleteColor;

        private boolean displayPercent;

        @Override
        public ProgressStringBar build() {
            return new ProgressStringBar(this);
        }

        @Override
        public ProgressStringBarBuilder setCurrent(double current) {
            this.current = current;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setTotal(double total) {
            this.total = Math.max(total, current);
            return this;
        }

        @Override
        public ProgressStringBarBuilder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setCompletedSymbol(String completedSymbol) {
            this.completedSymbol = completedSymbol;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setIncompleteSymbol(String incompleteSymbol) {
            this.incompleteSymbol = incompleteSymbol;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setCompletedColor(ColorMap color) {
            this.completeColor = color;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setIncompleteColor(ChatColor color) {
            this.incompleteColor = color.getName();
            return this;
        }

        @Override
        public ProgressStringBarBuilder setIncompleteColor(String hex) {
            this.incompleteColor = hex;
            return this;
        }

        @Override
        public ProgressStringBarBuilder setDisplayPercent(boolean displayPercent) {
            this.displayPercent = displayPercent;
            return this;
        }
    }
}
