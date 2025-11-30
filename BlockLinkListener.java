package com.example.blockwarp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BlockLinkListener implements Listener {

    private final Plugin plugin;
    private final WarpManager warpManager;

    public BlockLinkListener(Plugin plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only care about right-click block
        if (event.getClickedBlock() == null) return;
        // Avoid handling off-hand triggers twice
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // If player is sneaking and holding a stick with permission, link/unlink
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean isStick = item != null && item.getType() == Material.STICK;

        if (player.isSneaking() && isStick && player.hasPermission("blockwarp.linkblock")) {
            // attempt to link via looking up a last used warp? Simpler: message how to use /linkblock
            player.sendMessage("To link this block to a warp use: /linkblock <warpname> while looking at the block.");
            event.setCancelled(true);
            return;
        }

        // If this block is linked, warp player
        String warpName = warpManager.getLinkedWarp(block.getLocation());
        if (warpName != null) {
            if (!player.hasPermission("blockwarp.warp")) {
                player.sendMessage("You don't have permission to warp.");
                return;
            }
            Location dest = warpManager.getWarp(warpName);
            if (dest == null) {
                player.sendMessage("Warp '" + warpName + "' not found.");
                return;
            }
            player.teleport(dest);
            player.sendMessage("Warped to " + warpName + "!");
            event.setCancelled(true);
        }
    }
}
