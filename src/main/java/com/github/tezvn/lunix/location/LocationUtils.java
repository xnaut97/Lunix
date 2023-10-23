package com.github.tezvn.lunix.location;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LocationUtils {

    public static BlockFace getBlockFace(Player player, int radius) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, radius*radius);
        if (lastTwoTargetBlocks.size() != 2)
            return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    public static Location deserialize(FileConfiguration config, String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if(section == null)
            return null;
        Map<String, Object> map = Maps.newHashMap();
        section.getKeys(false).forEach(key -> {
            Object value = config.get(path + "." + key, null);
            if(value == null || value.toString().startsWith("MemorySection"))
                return;
            map.put(key, value);
        });
        return map.isEmpty() ? null : Location.deserialize(map);
    }
}
