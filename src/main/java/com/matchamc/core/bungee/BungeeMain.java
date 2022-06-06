package com.matchamc.core.bungee;

import com.matchamc.core.bungee.commands.AlertCmd;
import com.matchamc.core.bungee.commands.FindCmd;
import com.matchamc.core.bungee.commands.SendCmd;
import com.matchamc.core.bungee.commands.SendToAllCmd;
import com.matchamc.core.bungee.commands.ServerCmd;
import com.matchamc.core.bungee.util.Configurations;
import com.matchamc.discord.MatchaMC_Discord;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC - Bungee";
	private MatchaMC_Discord discordBot;
	private Configurations configurations;
	public static final BaseComponent[] PLUGIN_PREFIX = new ComponentBuilder("[").color(ChatColor.BLUE).append(new ComponentBuilder("MatchaMC").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder("]").color(ChatColor.BLUE).create()).append(new ComponentBuilder(" ").color(ChatColor.RESET).create()).create();
	public static final BaseComponent[] NON_PLAYER_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You must be a player to execute this command.").color(ChatColor.RED).create()).create();
	public static final BaseComponent[] NO_PERMISSION_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You do not have permission to perform this action.").color(ChatColor.RED).create()).create();
	public static final BaseComponent[] INSUFFICIENT_PARAMETERS_ERROR = new ComponentBuilder("Insufficient parameters!").color(ChatColor.RED).create();

	@Override
	public void onEnable() {
		MsgUtils.sendBungeeConsoleMessage("&aEnabling MatchaMC [Bungee] version " + getDescription().getVersion() + "...");
		configurations = new Configurations(this);
		registerCommand(new ServerCmd());
		registerCommand(new SendCmd());
		registerCommand(new FindCmd());
		registerCommand(new AlertCmd());
		registerCommand(new SendToAllCmd());
	}

	private void registerCommand(Command executor) {
		getProxy().getPluginManager().registerCommand(this, executor);
	}
}
