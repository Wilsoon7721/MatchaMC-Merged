package com.matchamc.core.bungee.util;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerConnectionFailCallback implements Callback<Boolean> {
	private ProxiedPlayer player;
	private boolean silent;
	private static final BaseComponent[] SERVER_CONNECTION_FAIL = new ComponentBuilder("Could not connect to server: ").color(ChatColor.RED).create();

	public ServerConnectionFailCallback(ProxiedPlayer player, boolean silent) {
		this.player = player;
		this.silent = silent;
	}

	@Override
	public void done(Boolean result, Throwable error) {
		if(!result) {
			if(silent)
				return;
			player.sendMessage(new ComponentBuilder().append(SERVER_CONNECTION_FAIL).append(new ComponentBuilder(error.getLocalizedMessage()).color(ChatColor.YELLOW).create()).create());
		}
	}

}
