package com.github.nullexceptionarg.execution.commands.subcommand;

import org.bukkit.entity.Player;
import com.github.nullexceptionarg.execution.Execution;
import com.github.nullexceptionarg.execution.commands.SubCommand;

import java.util.Arrays;

public class Ban extends SubCommand {
    @Override
    public String getPermission() {
        return "execute.ban";
    }

    @Override
    public String getLabel() {
        return "ban";
    }

    @Override
    public String getUsage() {
        return "/execute <player> ban <reason>";
    }

    @Override
    public String getDescription() {
        return "executes a player and then bans him";
    }

    @Override
    public void perform(Player mod, Player ply, String[] args) {
        if (mod.hasPermission(getPermission())) {
            String[] reason = Arrays.copyOfRange(args, 2, args.length);
            Execution.executeScript(mod, ply, getLabel(), String.join(" ", reason), "");
        } else {
            mod.sendMessage(getPermissionError());
        }
    }
}
