package com.matchamc.discord;

import java.util.Collection;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
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
	private boolean manageReactionsPermission = false;
	private Configuration config;

	public MatchaMC_Discord(Configurations configurations) {
		this.configurations = configurations;
		this.config = this.configurations.get("discord.yml");
		token = this.config.getString("token");
		if(token == null || token.isBlank()) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Failed to initialize Discord component - The plugin could not retrieve the token.");
			return;
		}
		MsgUtils.sendBungeeConsoleMessage("&3[MatchaMC - Discord] Successfully retrieved token! Ready to login to discord.");
	}


	public void setup() {
		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
		this.api = api;
		long error_output_channel_id = this.config.getLong("error-output-channel");
		serverId = this.config.getLong("server");
		if(this.api != null) {
			MsgUtils.sendBungeeConsoleMessage("&3[MatchaMC - Discord] Successfully logged in to Discord as &e" + api.getYourself().getDiscriminatedName() + "&3.");
		} else {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Failed to log in to Discord with the bot credentials. Setup interrupted.");
			return;
		}
		if(!this.api.getServerById(serverId).isPresent()) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Could not find the MatchaMC Discord server. Setup interrupted.");
			return;
		}
		discordServer = this.api.getServerById(serverId).get();
		Collection<PermissionType> allowedPermissions = discordServer.getAllowedPermissions(api.getYourself());
		// MANAGE_MESSAGES allows pin, delete messages as well as remove reactions.
		if(allowedPermissions.contains(PermissionType.MANAGE_MESSAGES) || allowedPermissions.contains(PermissionType.ADMINISTRATOR))
			manageReactionsPermission = true;
		MsgUtils.sendBungeeConsoleMessage("&e[MatchaMC - Discord] Verifying Discord configuration...");
		if(serverId == 0L) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Server ID key is empty.");
			return;
		}
		if(error_output_channel_id == 0L) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] Error Output Channel key is empty.");
			return;
		}
		if(!api.getServerById(serverId).isPresent()) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] The server id linked to a server that does not exist.");
			return;
		}
		if(!api.getServerById(serverId).get().getChannelById(error_output_channel_id).isPresent()) {
			MsgUtils.sendBungeeConsoleMessage("&c[MatchaMC - Discord] The error output channel id linked to a channel that does not exist.");
			return;
		}
		Message message = api.getMessageById(config.getLong("help-channel.message-to-check"), api.getTextChannelById(config.getLong("help-channel.text-channel-of-message")).get()).join();
		message.removeAllReactions();
		message.addReaction("mail:759413078843719750");

	}

	public boolean canRemoveReactions() {
		return manageReactionsPermission;
	}
}
