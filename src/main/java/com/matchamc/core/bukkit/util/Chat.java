package com.matchamc.core.bukkit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class Chat extends CoreCommand implements Listener {
	private Staffs staffs;
	private List<String> template = Arrays.asList("ALL", "STAFF", "ADMIN", "MANAGEMENT");
	private Map<UUID, Channel> playerChannels = new HashMap<>();
	private String permissionAll, permissionStaff, permissionAdmin, permissionManagement;

	public Chat(BukkitMain instance, Staffs staffs, String permissionNode) {
		super(instance, permissionNode);
		this.staffs = staffs;
		permissionAll = permissionNode + ".all";
		permissionStaff = permissionNode + ".staff";
		permissionAdmin = permissionNode = ".admin";
		permissionManagement = permissionNode + ".management";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			ArrayList<String> list = new ArrayList<>(template);
			if(!sender.hasPermission(permissionStaff))
				list.remove("STAFF");
			if(!sender.hasPermission(permissionAdmin))
				list.remove("ADMIN");
			if(!sender.hasPermission(permissionManagement))
				list.remove("MANAGEMENT");
			if(!sender.hasPermission(permissionAll))
				list.remove("ALL");
			String availableTypes = String.join(", ", list).trim();
			if(availableTypes.isBlank())
				availableTypes = "None.";
			sender.sendMessage(MsgUtils.color("&eAvailable types: &a" + availableTypes));
		}
		switch(args[0].toUpperCase()) {
		case "A":
		case "ALL":
			setPlayerChannel(player, Channel.ALL);
			sender.spigot().sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ALL").color(ChatColor.YELLOW).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "S":
		case "STAFF":
			setPlayerChannel(player, Channel.STAFF);
			sender.spigot().sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("STAFF").color(ChatColor.AQUA).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "AD":
		case "ADMIN":
			setPlayerChannel(player, Channel.ADMIN);
			sender.spigot().sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ADMIN").color(ChatColor.RED).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "M":
		case "MANAGEMENT":
		case "MGMT":
			setPlayerChannel(player, Channel.MANAGEMENT);
			sender.spigot().sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("MANAGEMENT").color(ChatColor.DARK_RED).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		default:
			setPlayerChannel(player, Channel.ALL);
			sender.spigot().sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ALL").color(ChatColor.YELLOW).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return template.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

	@EventHandler
	public void onPlayerLogin(PostLoginEvent event) {
		if(playerChannels.containsKey(event.getPlayer().getUniqueId()))
			return;
		playerChannels.put(event.getPlayer().getUniqueId(), Channel.ALL);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		Channel channel = playerChannels.get(player.getUniqueId());
		if(event.getMessage().startsWith("#")) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			String formattedMessage = event.getMessage().replaceFirst("#", "").strip();
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(!p.hasPermission(permissionStaff))
					continue;
				p.spigot().sendMessage(new ComponentBuilder("[S] ").color(ChatColor.AQUA).append(new ComponentBuilder("[Main Server] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(formattedMessage).color(ChatColor.AQUA).create()).create());
			}
		}
		if(channel == null || channel == Channel.ALL) {
			player.chat(event.getMessage());
			return;
		}
		if(channel == Channel.STAFF) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			// "&b[S] &3[%server%] &b%playerName%&7: &b%message%"
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(!p.hasPermission(permissionStaff))
					continue;
				p.spigot().sendMessage(new ComponentBuilder("[S] ").color(ChatColor.AQUA).append(new ComponentBuilder("[Main Server] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
		if(channel == Channel.ADMIN) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			// "&c[A] &3[%server%] &b%playerName%&7: &b%message%"
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(!p.hasPermission(permissionAdmin))
					continue;
				p.spigot().sendMessage(new ComponentBuilder("[A] ").color(ChatColor.RED).append(new ComponentBuilder("[Main Server] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
		if(channel == Channel.MANAGEMENT) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			// "&4[M] &3[%server%] &b%playerName%&7: &b%message%"
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(!p.hasPermission(permissionManagement))
					continue;
				p.spigot().sendMessage(new ComponentBuilder("[M] ").color(ChatColor.DARK_RED).append(new ComponentBuilder("[Main Server] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
	}

	public Channel getPlayerChannel(Player player) {
		if(playerChannels.containsKey(player.getUniqueId()))
			return playerChannels.get(player.getUniqueId());
		return Channel.ALL;
	}

	public void setPlayerChannel(Player player, Channel channel) {
		if(playerChannels.containsKey(player.getUniqueId()))
			playerChannels.remove(player.getUniqueId());
		playerChannels.put(player.getUniqueId(), channel);
	}

	public static enum Channel {
		ALL, STAFF, ADMIN, MANAGEMENT;
	}
}