package org.github.nullexceptionarg.execution;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.nullexceptionarg.execution.commands.CommandManager;
import org.github.nullexceptionarg.execution.listeners.ExecutionListener;

public class Execution extends JavaPlugin {
    private static Player executed = null;

    @Override
    public void onEnable(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ExecutionListener(),this);
        getCommand("setexecute").setExecutor(new CommandManager(this));
        getCommand("execute").setExecutor(new CommandManager(this));
    }

    @Override
    public void onDisable(){

    }

    public static Player getExecutedPlayer(){
        return executed;
    }

    public static void setExecutedPlayer(Player player){
        executed = player;
    }

    public static void executeScript(Execution execution, Player ply){
        FileConfiguration cfg  = execution.getConfig();
        setExecutedPlayer(ply);

    }


}
