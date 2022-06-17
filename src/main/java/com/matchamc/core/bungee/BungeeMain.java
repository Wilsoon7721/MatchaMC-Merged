package com.matchamc.core.bungee;

import com.matchamc.core.bungee.commands.AlertCmd;
import com.matchamc.core.bungee.commands.FindCmd;
import com.matchamc.core.bungee.commands.SendCmd;
import com.matchamc.core.bungee.commands.ServerCmd;
import com.matchamc.core.bungee.util.Chat;
import com.matchamc.core.bungee.util.Staffs;
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
	private Chat chat;
	private Staffs staffs;
	public static final BaseComponent[] PLUGIN_PREFIX = new ComponentBuilder("[").color(ChatColor.BLUE).append(new ComponentBuilder("MatchaMC").color(ChatColor.DARK_AQUA).create()).append(new ComponentBuilder("]").color(ChatColor.BLUE).create()).append(new ComponentBuilder(" ").color(ChatColor.RESET).create()).create();
	public static final BaseComponent[] NON_PLAYER_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You must be a player to execute this command.").color(ChatColor.RED).create()).create();
	public static final BaseComponent[] NO_PERMISSION_ERROR = new ComponentBuilder().append(PLUGIN_PREFIX).append(new ComponentBuilder("You do not have permission to perform this action.").color(ChatColor.RED).create()).create();
	public static final BaseComponent[] INSUFFICIENT_PARAMETERS_ERROR = new ComponentBuilder("Insufficient parameters!").color(ChatColor.RED).create();

	@Override
	public void onEnable() {
		// MsgUtils.sendBungeeConsoleMessage("&aEnabling MatchaMC [Bungee] version " + getDescription().getVersion() + "...");
		staffs = new Staffs(this, "core.bungee.staff");
		chat = new Chat(this, staffs, "chat", "core.bungee.chat");
		printIcon();
		getProxy().getPluginManager().registerListener(this, chat);
		getProxy().getPluginManager().registerListener(this, staffs);
		getProxy().registerChannel("MatchaMC_ServerPlugin");
		registerCommand(chat);
		registerCommand(new ServerCmd());
		registerCommand(new SendCmd());
		registerCommand(new FindCmd());
		registerCommand(new AlertCmd());
	}

	@Override
	public void onDisable() {
		getProxy().unregisterChannel("MatchaMC_ServerPlugin");
		staffs.commitNewStaffToFile();
	}

	private void registerCommand(Command executor) {
		getProxy().getPluginManager().registerCommand(this, executor);
	}

	private void printIcon() {
		// The escape codes messes up the perfect icon, but in the console it looks good - i think.
		MsgUtils.sendBukkitConsoleMessage("&a   __     ___     _____________       &3|");
		MsgUtils.sendBukkitConsoleMessage("&a  /  \\   /   \\   /   __________|    &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | /\\ \\_/ /\\  |  |  /              &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | | \\   /  | |  |  |                &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | |  | |   | |  |  |                 &3|    Running &bBungeeCord instance");
		MsgUtils.sendBukkitConsoleMessage("&a | |  | |   | |  |  |                 &3|     &b" + getDescription().getName() + " &3version &b" + getDescription().getVersion());
		MsgUtils.sendBukkitConsoleMessage("&a | |  |_|   | |  |  |                 &3|         &aThe plugin is enabling...");
		MsgUtils.sendBukkitConsoleMessage("&a | |        | |  |  |                 &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | |        | |  |  \\___________     &3|");
		MsgUtils.sendBukkitConsoleMessage("&a |_|        |_|  \\______________|    &3|");
	}
}
