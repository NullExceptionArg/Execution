package org.github.nullexceptionarg.execution.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.github.nullexceptionarg.execution.Execution;

import java.util.List;

public class CommandManager implements TabExecutor {
    private Execution instance;
    private FileConfiguration cfg;

    public CommandManager(Execution execution) {
        instance = execution;
        cfg = instance.getConfig();
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player ply = (Player) commandSender;
            if (command.getLabel().equalsIgnoreCase("setexecute")) {
                if (args.length == 2) {
                    Location loc = ply.getLocation();
                    float yaw = loc.getYaw();
                    String dir;
                    if (yaw > -45.0 && yaw <= 45.0) {
                        dir = "SOUTH";
                        loc.setYaw(0);
                    } else if (yaw > 45.0 & yaw <= 135.0) {
                        dir = "WEST";
                        loc.setYaw(90);
                    } else if (yaw > -135.0 && yaw <= -45.0) {
                        dir = "EAST";
                        loc.setYaw(-90);
                    } else {
                        dir = "NORTH";
                        loc.setYaw(180);
                    }
                    cfg.set("execution.location", loc);
                    cfg.set("execution.direction", dir);
                    cfg.set("execution.width", args[0]);
                    cfg.set("execution.height", args[1]);
                    instance.saveConfig();
                    ply.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Execution" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + "Execution location successfully set!");
                }
            }
        }
        if (command.getLabel().equalsIgnoreCase("execute")) {
            if (args.length == 1) {
                Player execution = Bukkit.getPlayer(args[0]);
                if (Execution.getExecutedPlayer() == null) {
                    Execution.executeScript(instance,execution);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
