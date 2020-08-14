package com.github.nullexceptionarg.execution.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.nullexceptionarg.execution.Execution;

import java.util.List;

public class ExecutionListener implements Listener {
    private String time;
    private String command;
    private String reason;
    private Player mod;
    private Player executed;
    private Execution instance = JavaPlugin.getPlugin(Execution.class);

    public ExecutionListener(Player pMod, Player pExecuted, String pCommand, String pReason, String pTime) {
        mod = pMod;
        command = pCommand;
        time = pTime;
        reason = pReason;
        executed = pExecuted;
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e) {
        if (e.getPlayer() != Execution.getExecutedPlayer()) return;
        e.setCancelled(true);

    }


    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e) {
        if (e.getEntity() != Execution.getExecutedPlayer()) return;
        List<ItemStack> lstItem = e.getDrops();

        mod.sendPluginMessage(instance, "execution:listener", getPluginMessage());
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        if (e.getPlayer() != Execution.getExecutedPlayer()) return;

        e.getPlayer().setHealth(0);
        mod.sendPluginMessage(instance, "execution:listener", getPluginMessage());
        HandlerList.unregisterAll(this);
    }


    public byte[] getPluginMessage() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(command);
        out.writeUTF(executed.getName());
        out.writeUTF(reason);
        out.writeUTF(time);
        return out.toByteArray();
    }


}
