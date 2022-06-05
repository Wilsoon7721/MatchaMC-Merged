package com.matchamc.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

import com.matchamc.core.bungee.util.Configurations;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.config.Configuration;

public class MatchaMC_Discord {
	private Configurations configurations;
	private DiscordApi api;
	private String token;
	private long serverId;
	private Server discordServer;
	private Configuration config;

	public MatchaMC_Discord(Configurations configurations) {
		this.configurations = configurations;
		this.config = this.configurations.get("discord.yml");
		token = this.config.getString("token");
		if(token == null || token.isBlank()) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Failed to initialize Discord component - The plugin could not retrieve the token.");
			return;
		}
		MsgUtils.sendBungeeConsoleMessage("&3[MatchaMC - Discord] Successfully retrieved token! Logging in...");
		setup();
	}


	public void setup() {
		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
		this.api = api;
		if(api == null) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Failed to login to Discord with the bot token provided.");
			return;
		}
		MsgUtils.sendBungeeConsoleMessage("&3[MatchaMC - Discord] Logged in as &b" + api.getYourself().getDiscriminatedName());
	}

	public Server getServer() {
		return api.getServerById(serverId).orElse(null);
	}
}
