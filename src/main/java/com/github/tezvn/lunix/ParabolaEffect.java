package com.github.tezvn.lunix;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ParabolaEffect {

    private final JavaPlugin plugin;

    public ParabolaEffect(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void displayParabola(Location startLocation, Location endLocation) {

        // Số điểm để xây dựng đường parabol
        int points = 20;

        // Thời gian chạy từ điểm bắt đầu đến điểm kết thúc (tính bằng tick)
        int duration = 40;

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (t <= 1.0) {
                    double x = interpolate(startLocation.getX(), endLocation.getX(), t);
                    double y = calculateParabolaY(startLocation.getY(), endLocation.getY(), t, 4, 1);
                    double z = interpolate(startLocation.getZ(), endLocation.getZ(), t);

                    Location particleLoc = new Location(startLocation.getWorld(), x, y, z);
                    System.out.println("Loc: " + particleLoc);
                    Objects.requireNonNull(startLocation.getWorld()).spawnParticle(Particle.HEART, particleLoc, 1);

                    t += 1.0 / points;
                } else {
                    this.cancel(); // Dừng nhiệm vụ khi đường parabol hoàn thành
                }
            }
        }.runTaskTimer(plugin, 0, duration / points);
    }

    private double calculateParabolaY(double startY, double endY, double t, int heightGain, int duration) {
        double a = -4 * heightGain / Math.pow(duration, 2);
        return a * Math.pow(t * duration - duration, 2) + startY;
    }

    private double interpolate(double start, double end, double t) {
        return start + (end - start) * t;
    }
}
