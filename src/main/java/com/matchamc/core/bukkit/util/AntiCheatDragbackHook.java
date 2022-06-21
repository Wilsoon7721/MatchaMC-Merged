package com.matchamc.core.bukkit.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

import AntiAuraAPI.DragBackEvent;
import me.jumper251.replay.api.ReplayAPI;

public class AntiCheatDragbackHook extends CoreCommand implements Listener {
	private HashSet<UUID> dragbackAlerts = new HashSet<>();
	public AntiCheatDragbackHook(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		Player p = (Player) sender;
		if(!(dragbackAlerts.contains(p.getUniqueId()))) {
			dragbackAlerts.add(p.getUniqueId());
			p.sendMessage(MsgUtils.color("&e(!) Your dragback alerts have been enabled."));
			return true;
		}
		dragbackAlerts.remove(p.getUniqueId());
		p.sendMessage(MsgUtils.color("&e(!) Your dragback alerts have been disabled."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

	@EventHandler
	public void dragbackMessage(DragBackEvent event) {
		dragbackAlerts.stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(player -> player != null).forEach(player -> player.sendMessage(MsgUtils.color("&e(!) &c" + AntiCheatTrigger.dragbackTarget + " &ewas dragged back for &c" + AntiCheatTrigger.dragbackHack + " &7(Ping: " + AntiCheatTrigger.dragbackPing + ")")));
		ReplayAPI.getInstance().recordReplay(AntiCheatTrigger.dragbackTarget, Bukkit.getConsoleSender(), Bukkit.getPlayer(AntiCheatTrigger.dragbackTarget));
	}

}
