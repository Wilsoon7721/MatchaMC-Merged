package com.matchamc.core.bungee;

import com.matchamc.shared.util.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeMain extends Plugin {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC - Bungee";
	public static final BaseComponent[] PLUGIN_PREFIX = new ComponentBuilder("[").color(ChatColor.BLUE).append(new ComponentBuilder("MatchaMC").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder("]").color(ChatColor.BLUE).create()).append(new ComponentBuilder(" ").color(ChatColor.RESET).create()).create();
	public static final BaseComponent[] NON_PLAYER_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You must be a player to execute this command.").color(ChatColor.RED).create()).create();
	public static final BaseComponent[] NO_PERMISSION_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You do not have permission to perform this action.").color(ChatColor.RED).create()).create();
	@Override
	public void onEnable() {
		MsgUtils.sendBungeeConsoleMessage("&aEnabling MatchaMC [Bungee] version " + getDescription().getVersion());
	}

	public void getConfig() {
		ConfigurationProvider.getProvider(YamlConfiguration.class);
	}
}
