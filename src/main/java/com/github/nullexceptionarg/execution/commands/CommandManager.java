package com.github.nullexceptionarg.execution.commands;

import com.github.nullexceptionarg.execution.commands.subcommand.Ban;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.github.nullexceptionarg.execution.Execution;
import com.github.nullexceptionarg.execution.commands.subcommand.TempBan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements TabExecutor {
    private Execution instance;
    private FileConfiguration cfg;
    private List<SubCommand> lstSubCommands;

    public CommandManager(Execution execution) {
        instance = execution;
        cfg = instance.getConfig();
        lstSubCommands = new ArrayList<>();
        lstSubCommands.add(new TempBan());
        lstSubCommands.add(new Ban());
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
                    Integer width = Integer.parseInt(args[0]);
                    Integer height = Integer.parseInt(args[1]);

                    cfg.set("execution.location", loc);
                    cfg.set("execution.direction", dir);
                    cfg.set("execution.width", width);
                    cfg.set("execution.height", height);
                    instance.saveConfig();
                    ply.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Execution" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + "Execution location successfully set!");
                }
            }
        }
        if (command.getLabel().equalsIgnoreCase("execute")) {
            if (Execution.getExecutedPlayer() != null) {
                commandSender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Execution" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + "Someone is already getting executed!");
                return true;
            }
            Player mod = null;
            if (commandSender instanceof Player)
                mod = (Player) commandSender;
            if (args.length == 1) {
                Player ply = Bukkit.getPlayer(args[0]);
                if (ply != null && Execution.getExecutedPlayer() == null) {
                    Execution.executeScript(mod, ply, "", "", "");
                }
            } else if (args.length > 1) {
                Player ply = Bukkit.getPlayer(args[0]);
                SubCommand sub = lstSubCommands.stream().filter(x -> x.getLabel().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if (sub == null && commandSender instanceof Player) {
                    helpMessage((Player) commandSender);
                    return true;
                } else {
                    sub.perform(mod, ply, args);
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> allowedChars = new ArrayList<>();
        allowedChars.add("s");
        allowedChars.add("m");
        allowedChars.add("h");
        allowedChars.add("d");
        allowedChars.add("w");
        allowedChars.add("mo");
        if (command.getLabel().equalsIgnoreCase("execute")) {
            if (args.length == 1) {
                return null;
            } else if (args.length == 2) {
                if (commandSender instanceof Player)
                    return lstSubCommands.stream().filter(x -> ((Player) commandSender).hasPermission(x.getPermission())).map(x -> x.getLabel()).collect(Collectors.toList());
                else
                    return lstSubCommands.stream().map(SubCommand::getLabel).collect(Collectors.toList());
            } else if (args.length == 3) {
                SubCommand sub = lstSubCommands.stream().filter(x -> x.getLabel().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if (sub == null)
                    return new ArrayList<>();
                else {
                    if (sub.getUsage().split(" ").length >= 3) {
                        String usage = sub.getUsage().split(" ")[2];
                        if (usage.contains("player")) {
                            return null;
                        } else {
                            if (sub.getLabel().equalsIgnoreCase("tempban")) {
                                String value = "X";
                                if (args[2].length() > 0) {
                                    try {
                                        Integer.parseInt(args[2]);
                                        value = args[2];
                                    } catch (Exception e) {
                                        return new ArrayList<>();
                                    }
                                }
                                List<String> lstTab = new ArrayList<>();
                                String finalValue = value;
                                lstTab.addAll(allowedChars.stream().map(x -> finalValue + x).collect(Collectors.toList()));
                                return lstTab;
                            } else if (sub.getLabel().equalsIgnoreCase("ban")) {
                                List<String> tab = new ArrayList<>();
                                tab.add("reason");
                                return tab;
                            }
                        }
                    }
                }
            }else if(args.length == 4) {
                SubCommand sub = lstSubCommands.stream().filter(x -> x.getLabel().equalsIgnoreCase(args[1])).findFirst().orElse(null);
                if (sub == null)
                    return new ArrayList<>();
                else{
                    if(sub.getLabel().equalsIgnoreCase("tempban")){
                        List<String> tab = new ArrayList<>();
                        tab.add("reason");
                        return tab;
                    }
                }
            }
            return new ArrayList<>();

        }


        return null;
    }

    public void helpMessage(Player p) {

    }

}
