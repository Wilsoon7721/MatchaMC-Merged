package com.matchamc.core.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.matchamc.shared.util.MsgUtils;

public class BukkitMain extends JavaPlugin {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC-Bukkit";
	public static final String PLUGIN_PREFIX = MsgUtils.color("&9&l[&3&lMatchaMC&9&l] &r");
	public static final String NON_PLAYER_ERROR = PLUGIN_PREFIX + MsgUtils.color("&cYou must be a player to execute this command.");
	
	@Override
	public void onEnable() {
		MsgUtils.sendBukkitConsoleMessage("&aEnabling MatchaMC [Bukkit/Spigot] version " + getDescription().getVersion());
	}
}
