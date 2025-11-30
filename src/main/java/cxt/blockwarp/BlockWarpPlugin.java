package com.example.blockwarp;

import org.bukkit.plugin.java.JavaPlugin;

public class BlockWarpPlugin extends JavaPlugin {

    private WarpManager warpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.warpManager = new WarpManager(this);
        getServer().getPluginManager().registerEvents(new BlockLinkListener(this, warpManager), this);
        getCommand("setwarp").setExecutor(new WarpCommand(this, warpManager));
        getCommand("warp").setExecutor(new WarpCommand(this, warpManager));
        getCommand("linkblock").setExecutor(new WarpCommand(this, warpManager));
        getLogger().info("BlockWarp enabled");
    }

    @Override
    public void onDisable() {
        warpManager.saveConfig();
        getLogger().info("BlockWarp disabled");
    }
}
