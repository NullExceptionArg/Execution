package com.github.nullexceptionarg.execution.commands.subcommand;

import com.github.nullexceptionarg.execution.commands.SubCommand;
import org.bukkit.entity.Player;
import com.github.nullexceptionarg.execution.Execution;

import java.util.Arrays;

public class TempBan extends SubCommand {
    @Override
    public String getPermission() {
        return "execute.tempban";
    }

    @Override
    public String getLabel() {
        return "tempban";
    }

    @Override
    public String getUsage() {
        return "/execute <player> tempban <time> <reason>";
    }

    @Override
    public String getDescription() {
        return "Executes a player and the tempbans him";
    }

    @Override
    public void perform(Player mod, Player ply, String[] args) {
        if (mod.hasPermission(getPermission())) {
            String[] reason = Arrays.copyOfRange(args, 3, args.length);
            Execution.executeScript(mod, ply, getLabel(), String.join(" ", reason), args[2]);
        } else {
            mod.sendMessage(getPermissionError());
        }
    }
}
