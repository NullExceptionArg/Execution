package com.github.nullexceptionarg.execution;


import com.github.nullexceptionarg.execution.commands.CommandManager;
import com.github.nullexceptionarg.execution.listeners.ExecutionListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Execution extends JavaPlugin {
    private static Player executed = null;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "execution:listener");
        getCommand("setexecute").setExecutor(new CommandManager(this));
        getCommand("execute").setExecutor(new CommandManager(this));
    }

    @Override
    public void onDisable() {

    }


    public static Player getExecutedPlayer() {
        return executed;
    }

    public static void setExecutedPlayer(Player player) {
        executed = player;
    }


    public static void executeScript(Player mod, Player ply, String command, String reason, String time) {
        Execution instance = JavaPlugin.getPlugin(Execution.class);
        FileConfiguration cfg = instance.getConfig();
        ExecutionListener listener = new ExecutionListener(mod, ply, command, reason, time);
        instance.getServer().getPluginManager().registerEvents(listener, instance);

        try {
            Location location = cfg.getLocation("execution.location");
            location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()).setType(Material.BARRIER);
            ply.teleport(location);
            Execution.setExecutedPlayer(ply);
            new BukkitRunnable() {
                Location loc = location;
                Integer width = (cfg.getInt("execution.width") - 1) / 2;
                Integer height = cfg.getInt("execution.height");
                Integer actualHeight = height;
                String dir = cfg.getString("execution.direction");
                Map<Integer, BlockData> lstBlocks = new HashMap<>();

                @Override
                public void run() {
                    if (lstBlocks.size() == 0) {
                        for (int x = -width; x <= width; x++) {
                            if (dir.equalsIgnoreCase("EAST") || dir.equalsIgnoreCase("WEST"))
                                lstBlocks.put(loc.getBlockZ() + x, loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + height, loc.getBlockZ() + x).getBlockData().clone());
                            else
                                lstBlocks.put(loc.getBlockX() + x, loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + height, loc.getBlockZ()).getBlockData().clone());
                        }
                    }

                    actualHeight--;
                    for (int x = -width; x <= width; x++) {
                        int y1 = loc.getBlockY() + actualHeight;
                        int y2 = loc.getBlockY() + actualHeight + 1;
                        if (dir.equalsIgnoreCase("EAST") || dir.equalsIgnoreCase("WEST")) {
                            loc.getWorld().getBlockAt(loc.getBlockX(), y1, loc.getBlockZ() + x).setBlockData(lstBlocks.get(loc.getBlockZ() + x));
                            loc.getWorld().getBlockAt(loc.getBlockX(), y2, loc.getBlockZ() + x).setType(Material.AIR);
                        } else {
                            loc.getWorld().getBlockAt(loc.getBlockX() + x, y1, loc.getBlockZ()).setBlockData(lstBlocks.get(loc.getBlockX() + x));
                            loc.getWorld().getBlockAt(loc.getBlockX() + x, y2, loc.getBlockZ()).setType(Material.AIR);
                        }
                    }
                    loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + actualHeight + 1, loc.getBlockZ()).setType(Material.CHAIN);

                    if (actualHeight == 0) {
                        ply.setHealth(0);
                        cancel();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                for (int y = 0; y <= height; y++) {
                                    for (int x = -width; x <= width; x++) {
                                        if (dir.equalsIgnoreCase("EAST") || dir.equalsIgnoreCase("WEST")) {
                                            if (y == height){
                                                loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ() + x).setBlockData(lstBlocks.get(loc.getBlockZ() + x));
                                            }
                                            else
                                                loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ() + x).setType(Material.AIR);
                                        } else {
                                            if (y == height)
                                                loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ()).setBlockData(lstBlocks.get(loc.getBlockZ() + x));
                                            else
                                                loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ()).setType(Material.AIR);
                                        }
                                    }
                                }
                                Execution.setExecutedPlayer(null);
                            }
                        }.runTaskLater(instance, 40);
                    }

                }
            }.runTaskTimer(instance, 5 * 20, 10);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The location is wrongly set in the configuration file. Please try to reset the location.");
        }


    }
}
