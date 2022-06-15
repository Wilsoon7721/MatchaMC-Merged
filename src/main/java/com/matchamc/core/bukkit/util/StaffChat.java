package com.matchamc.core.bukkit.util;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;
import com.matchamc.shared.Staffs;

public class StaffChat extends CoreCommand implements Listener {
	private Staffs staffs;
	private String permissionToggle, permissionSend, permissionReceive;
	public StaffChat(BukkitMain instance, Staffs staffs, String permissionNode) {
		super(instance, permissionNode);
		this.staffs = staffs;
		permissionToggle = permissionNode + ".toggle";
		permissionSend = permissionNode + ".send";
		permissionReceive = permissionNode + ".receive";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				return true;
			}
			Player player = (Player) sender;
			if(!staffs.isStaff(player) || !sender.hasPermission(permissionToggle)) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionToggle));
				return true;
			}
			boolean enabled = staffs.isStaffChatEnabled(player);
			if(enabled) {
				staffs.setStaffChatEnabled(player, false);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.staffchat.disabled").replace("%chat%", "&bstaff")));
				return true;
			}
			staffs.setStaffChatEnabled(player, true);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.staffchat.enabled").replace("%chat%", "&bstaff")));
			return true;
		}
		String message = String.join(" ", args).trim();
		sendStaffMessage(sender, message);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

	@EventHandler
	public void onStaffMessage(AsyncPlayerChatEvent event) {
		if(!staffs.isStaff(event.getPlayer()))
			return;
		if(staffs.isStaffChatEnabled(event.getPlayer()) || staffs.isStaffChatMessage(event.getMessage())) {
			event.setCancelled(true);
			sendStaffMessage(event.getPlayer(), event.getMessage());
		}
	}

	private void sendStaffMessage(CommandSender sender, String message) {
		if(!sender.hasPermission(permissionSend)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionSend));
			return;
		}
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.hasPermission(permissionReceive))
				continue;
			// TODO replace %server% with ???
			player.sendMessage(MsgUtils.color(instance.messages().getString("commands.staffchat.format.staff").replace("%server%", "f").replace("%playerName%", sender.getName()).replace("%message%", message.replaceFirst("#", "").trim())));
		}
	}
}
