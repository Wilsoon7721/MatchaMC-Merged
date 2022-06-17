package com.matchamc.core.bungee.util;

import java.util.Collections;

import org.bukkit.entity.Player;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.shared.MsgUtils;
import com.matchamc.shared.Staffs;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;

public class StaffChat extends Command implements TabExecutor {
	private BungeeMain instance;
	private Staffs staffs;
	private String playerServer;
	private String permissionNode, permissionToggle, permissionSend, permissionReceive;

	public StaffChat(BungeeMain instance, String name, Staffs staffs, String permissionNode) {
		super(name);
		this.instance = instance;
		this.staffs = staffs;
		this.permissionNode = permissionNode;
		permissionToggle = permissionNode + ".toggle";
		permissionSend = permissionNode + ".send";
		permissionReceive = permissionNode + ".receive";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command.").color(ChatColor.RED).create());
			return;
		}
		if(args.length == 0) {
			if(!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(new ComponentBuilder("You must be a player to execute this command.").color(ChatColor.RED).create());
				return;
			}
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if(!staffs.isStaff(player.getUniqueId()) || !sender.hasPermission(permissionToggle)) {
				sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command.").color(ChatColor.RED).create());
				return;
			}
			boolean enabled = staffs.isStaffChatEnabled(player);
			if(enabled) {
				staffs.setStaffChatEnabled(player, false);
				sender.sendMessage(new ComponentBuilder("You have disabled ").color(ChatColor.YELLOW).append(new ComponentBuilder("staff").color(ChatColor.AQUA).create()).append(new ComponentBuilder(" chat.").color(ChatColor.YELLOW).create()).create());
				return;
			}
			staffs.setStaffChatEnabled(player, true);
			sender.sendMessage(new ComponentBuilder("You have enabled ").color(ChatColor.YELLOW).append(new ComponentBuilder("staff").color(ChatColor.AQUA).create()).append(new ComponentBuilder(" chat.").color(ChatColor.YELLOW).create()).create());
			return;
		}
		String message = String.join(" ", args).trim();
		sendStaffMessage(sender, message);
		return;
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@EventHandler
	public void onStaffMessage(ChatEvent event) {
		if(!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
		if(!staffs.isStaff(sender.getUniqueId()))
			return;
		if(staffs.isStaffChatEnabled(sender) || staffs.isStaffChatMessage(event.getMessage())) {
			event.setCancelled(true);
			sendStaffMessage(sender, event.getMessage());
		}
	}

	private void sendStaffMessage(CommandSender sender, String message) {
		if(!sender.hasPermission(permissionSend)) {
			sender.sendMessage(new ComponentBuilder("You do not have permission to execute this command.").color(ChatColor.RED).create());
			return;
		}
		String username = "";
		String formattedMessage = message.trim();
		if(staffs.isStaffChatMessage(message))
			formattedMessage = message.replaceFirst("#", "").trim();
		if(!(sender instanceof Player)) {
			username = MsgUtils.color("&cCONSOLE");
			playerServer = "Main Server";
		}
		for(ProxiedPlayer player : instance.getProxy().getPlayers()) {
			if(!player.hasPermission(permissionReceive))
				continue;
			// "&b[S] &3[%server%] &b%playerName%&7: &b%message%"
			player.sendMessage(new ComponentBuilder("[S] ").color(ChatColor.AQUA).append(new ComponentBuilder("[%server%] ".replace("%server%", playerServer)).color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(username).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(formattedMessage).color(ChatColor.AQUA).create()).create());
		}
	}
}
