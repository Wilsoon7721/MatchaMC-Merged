package com.matchamc.core.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.matchamc.shared.util.MsgUtils;

public class BukkitMain extends JavaPlugin implements PluginMessageListener {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC-Bukkit";
	public static final String PLUGIN_PREFIX = MsgUtils.color("&9&l[&3&lMatchaMC&9&l] &r");
	public static final String NON_PLAYER_ERROR = PLUGIN_PREFIX + MsgUtils.color("&cYou must be a player to execute this command.");

	@Override
	public void onEnable() {
		MsgUtils.sendBukkitConsoleMessage("&aEnabling MatchaMC [Bukkit/Spigot] version " + getDescription().getVersion());
	}

	// All subservers must have this plugin or else they are unable to receive messages from /sendtoall.
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("BungeeCord"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(!subchannel.equals("Forward"))
			return;

	}
}
