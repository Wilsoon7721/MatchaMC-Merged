package com.matchamc.core.bungee.util;

import java.util.Map;

import com.matchamc.core.bungee.BungeeMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;

public class ServerManager {
	private ServerManager() {}
	private static final Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

	public static void joinServer(ProxiedPlayer player, ServerInfo server) {
		if(!canAccess(player, server)) {
			player.sendMessage(new ComponentBuilder().append(BungeeMain.PLUGIN_PREFIX).append(new ComponentBuilder("You are restricted from accessing this server.").color(ChatColor.RED).create()).create());
			return;
		}
		player.connect(server, new ServerConnectionFailCallback(player, false), Reason.COMMAND);
	}

	public static void sendToServer(ProxiedPlayer target, ServerInfo server) {
		ServerConnectRequest request = ServerConnectRequest.builder().reason(Reason.PLUGIN).target(server).build();
		target.connect(request);
	}

	public static boolean serverExists(String serverName) {
		if(!(servers.containsKey(serverName)))
			return false;
		return true;
	}

	public static boolean canAccess(ProxiedPlayer player, ServerInfo server) {
		// This method accounts for this plugin's own way of identifying whether a player has permission to join a server using classic permissions.
		if(server.canAccess(player) && player.hasPermission("core.bungee.server." + server.getName()))
			return true;
		return false;
	}
}
