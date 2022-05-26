package com.matchamc.core.bungee.commands;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.core.bungee.util.ServerManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SendCmd extends Command implements TabExecutor {

	public SendCmd() {
		super("send");

	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("core.bungee.send")) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		if(args.length >= 0 && args.length < 2) {
			sender.sendMessage(BungeeMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(new ComponentBuilder("Usage: /send <player/current/all> <server>").color(ChatColor.RED).create());
			return;
		}
		if(args[0].equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(new ComponentBuilder("Do not use /send with your own name. ").color(ChatColor.RED).append(new ComponentBuilder("Use /server instead").event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/server")).color(ChatColor.YELLOW).bold(true).create()).create());
			return;
		}
		String serverName = args[1];
		if(!ServerManager.serverExists(serverName)) {
			sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("This server does not exist.").color(ChatColor.RED).create()).create());
			return;
		}
		ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args[0].equalsIgnoreCase("all")) {
			Set<ProxiedPlayer> all = ProxyServer.getInstance().getPlayers().stream().filter(p -> !p.getName().equalsIgnoreCase(sender.getName())).collect(Collectors.toSet());
			int alreadyConnected = 0;
			for(ProxiedPlayer connectedPlayer : all) {
				if(connectedPlayer.getServer().getInfo().getName().equals(server.getName())) {
					alreadyConnected++;
					continue;
				}
				ServerManager.sendToServer(connectedPlayer, server);
			}
			sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("Successfully sent ").color(ChatColor.GREEN).append(new ComponentBuilder(String.valueOf(all.size() - alreadyConnected)).color(ChatColor.GOLD).create()).append(new ComponentBuilder(" players to your server.").color(ChatColor.GREEN).create()).create()).create());
			if(alreadyConnected != 0) {
				sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder(String.valueOf(alreadyConnected)).color(ChatColor.RED).create()).append(new ComponentBuilder(" players were already connected.").color(ChatColor.RED).create()).create());
			}
			return;
		}
		if(args[0].equalsIgnoreCase("current")) {
			// No need to implement already connected players as all players in the current server would be sent to the target server so there will be no already connected players.
			if(sender == ProxyServer.getInstance().getConsole()) {
				sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("The plugin is unable to determine your current server, and cannot send all players on this server to another server.").color(ChatColor.RED).create()).create());
				return;
			}
			Set<ProxiedPlayer> connected = player.getServer().getInfo().getPlayers().stream().filter(p -> !p.getName().equals(sender.getName())).collect(Collectors.toSet());
			for(ProxiedPlayer connectedPlayer : connected) {
				ServerManager.sendToServer(connectedPlayer, server);
			}
			return;
		}
		ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(new ComponentBuilder("This player could not be found.").color(ChatColor.RED).create());
			return;
		}
		if(target.getServer().getInfo().getName().equals(server.getName())) {
			sender.sendMessage(new ComponentBuilder("This player is already connected to the target server!").color(ChatColor.RED).create());
			return;
		}
		ServerManager.sendToServer(target, server);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).filter(str -> !str.equalsIgnoreCase(sender.getName())).filter(str -> str.startsWith(args[0])).collect(Collectors.toSet());
		}
		if(args.length == 1) {
			return ProxyServer.getInstance().getServers().keySet().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toSet());
		}
		return Collections.emptyList();
	}
}
