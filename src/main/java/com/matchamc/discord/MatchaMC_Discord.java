package com.matchamc.discord;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;

import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.shared.MsgUtils;

public class MatchaMC_Discord {
	private Configurations configurations;
	private DiscordApi api;
	private List<PermissionType> requiredPermissions = Arrays.asList(PermissionType.MANAGE_MESSAGES);
	private boolean enabled = false;
	public MatchaMC_Discord(Configurations configurations) {
		if(!(this.configurations.exists("discord.yml"))) {
			MsgUtils.sendBukkitConsoleMessage("&e[MatchaMC - Discord] The discord component has been disabled as 'discord.yml' was not found. If this is the first time the plugin is running, please ignore this message as the file will be created.");
			enabled = false;
			this.configurations.create("discord.yml");
			return;
		}
		// Verify Config
		YamlConfiguration config = configurations.get("discord.yml");
		if(config.getBoolean("discord.bot-started") && config.getString("discord.started-on-server") != null) {
			MsgUtils.sendBukkitConsoleMessage("&e[MatchaMC - Discord] Skipping Discord initialization as it is already active on another server instance. Check config if this is in error.");
			return;
		}
		boolean verificationResult = verifyConfig();
		if(!verificationResult) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] The component cannot be enabled as one or more configuration values failed the check. Please verify the 'discord.yml' file.");
			enabled = false;
			return;
		}
		enabled = true;
		YamlConfiguration yc = configurations.get("discord.yml");
		MsgUtils.sendBukkitConsoleMessage("&a[MatchaMC - Discord] Connecting to discord with the bot token, please wait...");
		String botToken = yc.getString("bot-token");
		DiscordApi api = new DiscordApiBuilder().setToken(botToken).setAllNonPrivilegedIntents().login().exceptionally(ExceptionLogger.get()).join();
		if(api != null) {
			MsgUtils.sendBukkitConsoleMessage("&a[MatchaMC - Discord] Successfully logged into Discord as &e" + api.getYourself().getDiscriminatedName() + "&a.");
			this.api = api;
			setup();
			return;
		}
		MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] Failed to login to Discord.");
	}

	private void setup() {
		if(!hasAllRequiredPermissions()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] The discord bot does not have all the required permissions to operate to its fullest functionality on this server.");
			MsgUtils.sendBukkitConsoleMessage("&c                  -----> Missing permissions: [" + (requiredPermissions.stream().filter(type -> !getServer().getAllowedPermissions(api.getYourself()).contains(type)).map(PermissionType::name).collect(Collectors.joining(", ")) + "]"));
			return;
		}
		// TODO Change 'discord' properties - To tell other bukkit instances not to run discord bot again. Change back when all instances stop
		File discordConfig = configurations.getFile("discord.yml");
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(discordConfig);
		yc.set("discord.bot-started", true);
		yc.set("started-on-server", "UNKNOWN");
		try {
			yc.save(discordConfig);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		YamlConfiguration.loadConfiguration(discordConfig);
	}

	public boolean isComponentEnabled() {
		return enabled;
	}

	public DiscordApi api() {
		return api;
	}

	public boolean hasAllRequiredPermissions() {
		Server server = getServer();
		if(server == null)
			return false;
		Collection<PermissionType> allowedPermissions = server.getAllowedPermissions(api.getYourself());
		if(allowedPermissions.containsAll(requiredPermissions) || allowedPermissions.contains(PermissionType.ADMINISTRATOR))
			return true;
		return false;
	}

	public Server getServer() {
		return api.getServerById(getDiscordConfig().getLong("listen-to-server-id")).orElse(null);
	}

	private boolean verifyConfig() {
		boolean failed = false;
		YamlConfiguration yc = this.configurations.get("discord.yml");
		String botToken = yc.getString("bot-token");
		Long serverId = yc.getLong("listen-to-server-id");
		Long errorOutputChannelId = yc.getLong("server-error-output-channel-id");
		Long triggerMessageChannelId = yc.getLong("ticket.trigger.reaction.channel-id-where-message-is");
		Long triggerMessageId = yc.getLong("ticket.trigger.reaction.message-id");
		Long ticketCreationCategoryId = yc.getLong("ticket.creation.category-id-to-create-ticket");
		String emoteMentionTag = yc.getString("ticket.trigger.reaction.emote-mention-tag");
		String emoteReactionTag = yc.getString("ticket.trigger.reaction.emote-reaction-tag");
		String ticketChannelFormat = yc.getString("ticket.creation.ticket-channel-format");
		if(botToken == null || botToken.isBlank()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'bot-token' - configuration value missing or empty!");
			failed = true;
		}
		if(serverId == 0 || serverId == null) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'listen-to-server-id' - configuration value missing or empty!");
			failed = true;
		}
		if(errorOutputChannelId == 0 || errorOutputChannelId == null) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'server-error-output-channel-id' - configuration value missing or empty!");
			failed = true;
		}
		if(triggerMessageChannelId == 0 || triggerMessageChannelId == null) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.trigger.reaction.channel-id-where-message-is' - configuration value missing or empty!");
			failed = true;
		}
		if(triggerMessageId == 0 || triggerMessageId == null) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.trigger.reaction.message-id' - configuration value missing or empty!");
			failed = true;
		}
		if(ticketCreationCategoryId == 0 || ticketCreationCategoryId == null) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.creation.category-id-to-create-ticket' - configuration value missing or empty!");
			failed = true;
		}
		if(emoteMentionTag == null || emoteMentionTag.isBlank()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.trigger.reaction.emote-mention-tag' - configuration value missing or empty!");
			failed = true;
		}
		if(emoteReactionTag == null || emoteReactionTag.isBlank()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.trigger.reaction.emote-reaction-tag' - configuration value missing or empty!");
			failed = true;
		}
		if(ticketChannelFormat == null || ticketChannelFormat.isBlank()) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Discord] 'ticket.creation.ticket-channel-format' - configuration value missing or empty!");
			failed = true;
		}
		if(failed) 
			return false;
		return true;
	}

	public YamlConfiguration getDiscordConfig() {
		return configurations.get("discord.yml");
	}

	public int getTicketNumberAndIncrement() {
		File file = configurations.getFile("discord.yml");
		YamlConfiguration config = getDiscordConfig();
		int ticketNo = config.getInt("ticket.next_ticket_no");
		config.set("ticket.next_ticket_no", (ticketNo + 1));
		try {
			config.save(file);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cFailed to increment ticket number - the file could not save.");
			ex.printStackTrace();
			return ticketNo;
		}
		YamlConfiguration.loadConfiguration(file);
		return ticketNo;
	}
}
