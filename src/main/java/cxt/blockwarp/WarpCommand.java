package com.example.blockwarp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private final BlockWarpPlugin plugin;
    private final WarpManager warpManager;

    public WarpCommand(BlockWarpPlugin plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String cmd = command.getName().toLowerCase();
        if (cmd.equals("setwarp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can run this.");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("blockwarp.setwarp")) {
                p.sendMessage("No permission.");
                return true;
            }
            if (args.length < 1) {
                p.sendMessage("Usage: /setwarp <name>");
                return true;
            }
            String name = args[0].toLowerCase();
            Location loc = p.getLocation();
            warpManager.setWarp(name, loc);
            warpManager.saveConfig();
            p.sendMessage("Warp '" + name + "' set.");
            return true;
        }

        if (cmd.equals("warp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can run this.");
                return true;
            }
            Player p = (Player) sender;
            if (args.length < 1) {
                p.sendMessage("Usage: /warp <name>");
                return true;
            }
            String name = args[0].toLowerCase();
            if (!warpManager.warpExists(name)) {
                p.sendMessage("Warp '" + name + "' does not exist.");
                return true;
            }
            Location dest = warpManager.getWarp(name);
            if (dest == null) {
                p.sendMessage("Warp '" + name + "' not found.");
                return true;
            }
            p.teleport(dest);
            p.sendMessage("Warped to " + name + "!");
            return true;
        }

        if (cmd.equals("linkblock")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can run this.");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("blockwarp.linkblock")) {
                p.sendMessage("No permission.");
                return true;
            }
            if (args.length < 1) {
                p.sendMessage("Usage: /linkblock <warpName>");
                return true;
            }
            String warpName = args[0].toLowerCase();
            if (!warpManager.warpExists(warpName)) {
                p.sendMessage("Warp '" + warpName + "' does not exist. Use /setwarp first.");
                return true;
            }

            // Find the block the player is looking at (within 6 blocks)
            Block target = p.getTargetBlockExact(6);
            if (target == null) {
                p.sendMessage("No block in range. Look at a block within 6 blocks and try again.");
                return true;
            }
            Location blockLoc = target.getLocation();
            warpManager.linkBlock(blockLoc, warpName);
            warpManager.saveConfig();
            p.sendMessage("Linked block at " + blockLoc.getBlockX() + "," + blockLoc.getBlockY() + "," + blockLoc.getBlockZ() + " to warp " + warpName);
            return true;
        }

        return false;
    }
}
