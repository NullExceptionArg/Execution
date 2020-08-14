package com.github.nullexceptionarg.execution.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class SubCommand {
    public abstract String getPermission();

    public String getPermissionError() {
        return ChatColor.DARK_RED + "[" + ChatColor.RED + "Execution" + ChatColor.DARK_RED + "] " + "You do not have the permission to use the command " + ChatColor.YELLOW + ChatColor.BOLD + getLabel();
    }

    public abstract String getLabel();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract void perform(Player mod, Player ply, String[] args);
}
