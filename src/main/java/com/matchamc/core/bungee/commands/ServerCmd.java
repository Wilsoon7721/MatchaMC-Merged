package com.matchamc.core.bungee.commands;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.core.bungee.util.ServerManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class ServerCmd extends Command implements TabExecutor {
	private Map<String, ServerInfo> servers;

	public ServerCmd() {
		super("server");
		servers = ProxyServer.getInstance().getServers();
	}

	/*
	 * NOTICE: THIS COMMAND WILL OVERRIDE Bungeecord's /server
	 */
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(BungeeMain.NON_PLAYER_ERROR);
			return;
		}
		if(!sender.hasPermission("core.bungee.server")) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args.length == 0) {
			listServers(player);
			return;
		}
		String serverName = args[0];
		if(!ServerManager.serverExists(serverName)) {
			sender.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("This server does not exist.").color(ChatColor.RED).create()).create());
			return;
		}
		ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
		ServerManager.joinServer(player, server);
	}

	private void listServers(ProxiedPlayer player) {
		String serverList = servers.keySet().stream().collect(Collectors.joining(", "));
		player.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("You must specify a server to connect to!").color(ChatColor.RED).create()).create());
		player.sendMessage(new ComponentBuilder("Available servers: ").color(ChatColor.GOLD).append(new ComponentBuilder(serverList).color(ChatColor.WHITE).create()).create());
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return servers.keySet().stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toSet());
		}
		return Collections.emptyList();
	}
}
