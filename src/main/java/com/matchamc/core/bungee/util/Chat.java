package com.matchamc.core.bungee.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.matchamc.core.bungee.BungeeMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;

public class Chat extends Command implements TabExecutor, Listener {
	private BungeeMain instance;
	private Staffs staffs;
	private List<String> template = Arrays.asList("ALL", "STAFF", "ADMIN", "MANAGEMENT");
	private Map<UUID, Chat.Channel> playerChannels = new HashMap<>();
	private String permissionNode, permissionAll, permissionStaff, permissionAdmin, permissionManagement;

	public Chat(BungeeMain instance, Staffs staffs, String name, String permissionNode) {
		super(name);
		this.instance = instance;
		this.staffs = staffs;
		this.permissionNode = permissionNode;
		permissionAll = permissionNode + ".all";
		permissionStaff = permissionNode + ".staff";
		permissionAdmin = permissionNode + ".admin";
		permissionManagement = permissionNode + ".management";
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(BungeeMain.NON_PLAYER_ERROR);
			return;
		}
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args.length == 0) {
			HashSet<String> list = new HashSet<>(template);
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
			sender.sendMessage(new ComponentBuilder("Available types: ").color(ChatColor.YELLOW).append(new ComponentBuilder(availableTypes).color(ChatColor.GREEN).create()).create());
			return;
		}
		String arg = args[0];
		switch(arg.toUpperCase()) {
		case "A":
		case "ALL":
			setPlayerChannel(player, Channel.ALL);
			sender.sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ALL").color(ChatColor.YELLOW).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "S":
		case "STAFF":
			setPlayerChannel(player, Channel.STAFF);
			sender.sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("STAFF").color(ChatColor.AQUA).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "AD":
		case "ADMIN":
			setPlayerChannel(player, Channel.ADMIN);
			sender.sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ADMIN").color(ChatColor.RED).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		case "M":
		case "MANAGEMENT":
		case "MGMT":
			setPlayerChannel(player, Channel.MANAGEMENT);
			sender.sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("MANAGEMENT").color(ChatColor.DARK_RED).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		default:
			setPlayerChannel(player, Channel.ALL);
			sender.sendMessage(new ComponentBuilder("You are now in the ").color(ChatColor.GREEN).append(new ComponentBuilder("ALL").color(ChatColor.YELLOW).append(new ComponentBuilder(" channel.").color(ChatColor.GREEN).create()).create()).create());
			break;
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PostLoginEvent event) {
		if(playerChannels.containsKey(event.getPlayer().getUniqueId()))
			return;
		playerChannels.put(event.getPlayer().getUniqueId(), Channel.ALL);
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		if(!(event.getSender() instanceof ProxiedPlayer))
			return;
		event.setCancelled(true);
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		Channel channel = playerChannels.get(player.getUniqueId());
		if(event.getMessage().startsWith("#")) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			String formattedMessage = event.getMessage().replaceFirst("#", "").strip();
			for(ProxiedPlayer p : instance.getProxy().getPlayers()) {
				if(!p.hasPermission(permissionStaff))
					continue;
				p.sendMessage(new ComponentBuilder("[S] ").color(ChatColor.AQUA).append(new ComponentBuilder("[" + player.getServer().getInfo().getName() + "] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(formattedMessage).color(ChatColor.AQUA).create()).create());
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
			for(ProxiedPlayer p : instance.getProxy().getPlayers()) {
				if(!p.hasPermission(permissionStaff))
					continue;
				p.sendMessage(new ComponentBuilder("[S] ").color(ChatColor.AQUA).append(new ComponentBuilder("[" + player.getServer().getInfo().getName() + "] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
		if(channel == Channel.ADMIN) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			// "&c[A] &3[%server%] &b%playerName%&7: &b%message%"
			for(ProxiedPlayer p : instance.getProxy().getPlayers()) {
				if(!p.hasPermission(permissionAdmin))
					continue;
				p.sendMessage(new ComponentBuilder("[A] ").color(ChatColor.RED).append(new ComponentBuilder("[" + player.getServer().getInfo().getName() + "] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
		if(channel == Channel.MANAGEMENT) {
			if(!staffs.isStaff(player.getUniqueId())) {
				player.chat(event.getMessage());
				return;
			}
			// "&4[M] &3[%server%] &b%playerName%&7: &b%message%"
			for(ProxiedPlayer p : instance.getProxy().getPlayers()) {
				if(!p.hasPermission(permissionManagement))
					continue;
				p.sendMessage(new ComponentBuilder("[M] ").color(ChatColor.DARK_RED).append(new ComponentBuilder("[" + player.getServer().getInfo().getName() + "] ").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder(player.getName()).color(ChatColor.AQUA).create()).append(new ComponentBuilder(": ").color(ChatColor.GRAY).create()).append(new ComponentBuilder(event.getMessage()).color(ChatColor.AQUA).create()).create());
			}
		}
	}

	public void setPlayerChannel(ProxiedPlayer player, Channel channel) {
		if(playerChannels.containsKey(player.getUniqueId()))
			playerChannels.remove(player.getUniqueId());
		playerChannels.put(player.getUniqueId(), channel);
	}

	public static enum Channel {
		ALL, STAFF, ADMIN, MANAGEMENT;
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 0)
			return template.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}
	
	
}
