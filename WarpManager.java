package com.example.blockwarp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class WarpManager {

    private final Plugin plugin;
    private final FileConfiguration cfg;

    // warpName -> Location
    private final Map<String, Location> warps = new HashMap<>();

    // blockKey -> warpName
    private final Map<String, String> blockLinks = new HashMap<>();

    public WarpManager(Plugin plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        warps.clear();
        blockLinks.clear();

        ConfigurationSection ws = cfg.getConfigurationSection("warps");
        if (ws != null) {
            for (String key : ws.getKeys(false)) {
                ConfigurationSection w = ws.getConfigurationSection(key);
                if (w == null) continue;
                String worldName = w.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                double x = w.getDouble("x");
                double y = w.getDouble("y");
                double z = w.getDouble("z");
                float yaw = (float) w.getDouble("yaw", 0.0);
                float pitch = (float) w.getDouble("pitch", 0.0);
                Location loc = new Location(world, x, y, z, yaw, pitch);
                warps.put(key.toLowerCase(), loc);
            }
        }

        ConfigurationSection bl = cfg.getConfigurationSection("blocklinks");
        if (bl != null) {
            for (String key : bl.getKeys(false)) {
                String warpName = bl.getString(key);
                if (warpName != null) blockLinks.put(key, warpName.toLowerCase());
            }
        }
    }

    public void saveConfig() {
        // warps
        ConfigurationSection ws = cfg.getConfigurationSection("warps");
        if (ws == null) {
            cfg.set("warps", null);
            ws = cfg.createSection("warps");
        } else {
            for (String k : ws.getKeys(false)) ws.set(k, null);
        }
        for (Map.Entry<String, Location> e : warps.entrySet()) {
            String name = e.getKey();
            Location l = e.getValue();
            ConfigurationSection s = cfg.createSection("warps." + name);
            s.set("world", l.getWorld().getName());
            s.set("x", l.getX());
            s.set("y", l.getY());
            s.set("z", l.getZ());
            s.set("yaw", l.getYaw());
            s.set("pitch", l.getPitch());
        }

        // blocklinks
        ConfigurationSection bl = cfg.getConfigurationSection("blocklinks");
        if (bl == null) {
            cfg.set("blocklinks", null);
            bl = cfg.createSection("blocklinks");
        } else {
            for (String k : bl.getKeys(false)) bl.set(k, null);
        }
        for (Map.Entry<String, String> e : blockLinks.entrySet()) {
            cfg.set("blocklinks." + e.getKey(), e.getValue());
        }

        plugin.saveConfig();
    }

    public void setWarp(String name, Location location) {
        warps.put(name.toLowerCase(), location.clone());
    }

    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean warpExists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public void linkBlock(Location blockLocation, String warpName) {
        String key = blockKey(blockLocation);
        blockLinks.put(key, warpName.toLowerCase());
    }

    public void unlinkBlock(Location blockLocation) {
        blockLinks.remove(blockKey(blockLocation));
    }

    public String getLinkedWarp(Location blockLocation) {
        return blockLinks.get(blockKey(blockLocation));
    }

    private String blockKey(Location l) {
        return l.getWorld().getName() + ";" + Math.floor(l.getX()) + ";" + Math.floor(l.getY()) + ";" + Math.floor(l.getZ());
    }
}
