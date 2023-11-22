package com.github.tezvn.lunix;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.noise.LatticeOrientation;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.NoiseQualitySimplex;
import org.spongepowered.noise.module.source.Perlin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Getter
public class WorldGeneration {

    private final World world;

    public WorldGeneration(Plugin plugin) {
        World world = Bukkit.getWorld("sky_realm");
        if (world != null) {
            Bukkit.unloadWorld(world.getName(), false);
            try {
                FileUtils.deleteDirectory(new File("sky_realm"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        WorldCreator creator = new WorldCreator("sky_realm");
        creator.generator(new ChunkGenerator() {

            public void generateSurface(WorldInfo info, Random random, int x, int z, ChunkData data) {
                for (int y = info.getMinHeight(); y < info.getMaxHeight(); y++) {
                    data.setBlock(x, y, z, Material.AIR);
                }

                for (int cx = 0; x < 16; x++) {
                    for (int cz = 0; z < 16; z++) {
                        // Tính toán giá trị noise và điều chỉnh nó
                        double noiseValue = Noise.valueCoherentNoise3D(100, 100, 100,
                                new Random().nextInt(), NoiseQuality.BEST);

                        // Tạo địa hình
//                        for (int y = 0; y < height; y++) {
//                            data.setBlock(cx, y, cz, Material.GRASS_BLOCK);
//                        }
                    }
                }
            }

            public boolean shouldGenerateNoise() {
                return false;
            }

            public boolean shouldGenerateCaves() {
                return false;
            }
        });
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.type(WorldType.FLAT);
        this.world = creator.createWorld();
        if (world != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> player.teleport(world.getSpawnLocation()));
                }
            }.runTaskLater(plugin, 40);
        }
    }

}
