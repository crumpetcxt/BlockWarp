package yourname.blockwarp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class WarpManager {

    private final BlockWarp plugin;

    public WarpManager(BlockWarp plugin) {
        this.plugin = plugin;
    }

    public Location getWarpLocation(String name) {
        FileConfiguration config = plugin.getConfig();

        String path = "warps." + name;
        if (!config.contains(path)) return null;

        return new Location(
                Bukkit.getWorld(config.getString(path + ".world")),
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z")
        );
    }

    public String getWarpForBlock(Material block) {
        return plugin.getConfig().getString("blocks." + block.name() + ".warp");
    }
}
