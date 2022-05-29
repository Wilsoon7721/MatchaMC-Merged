package com.matchamc.core.bukkit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.matchamc.core.bukkit.commands.ClearChatCmd;
import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.shared.util.MsgUtils;

public class BukkitMain extends JavaPlugin implements PluginMessageListener {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC - Bukkit";
	public static String NON_PLAYER_ERROR, NO_PERMISSION_ERROR, INSUFFICIENT_PARAMETERS_ERROR;
	private static Configurations configurations;
	private static YamlConfiguration messages;
	private static BukkitMain instance;
	@Override
	public void onEnable() {
		MsgUtils.sendBukkitConsoleMessage("&aEnabling MatchaMC [Bukkit/Spigot] version " + getDescription().getVersion());
		instance = this;
		configurations = new Configurations(this);
		configurations.create("messages.yml");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		getCommand("clearchat").setExecutor(new ClearChatCmd(this, "core.clearchat"));
		reloadMessages();
	}

	private static void reloadMessages() {
		messages = configurations.get("messages.yml");
		NON_PLAYER_ERROR = messages.getString("non_player_error");
		NO_PERMISSION_ERROR = messages.getString("no_permission_error");
		INSUFFICIENT_PARAMETERS_ERROR = messages.getString("insufficient_parameters_error");
	}

	public YamlConfiguration messages() {
		return messages;
	}

	public static void reloadData() {
		reloadMessages();
		instance.reloadConfig();
	}

	// All subservers must have this plugin or else they are unable to receive messages from /sendtoall.
	// SEND TO ALL MESSAGES ARE SENT IN THE FORMAT - data_1VSGK (command)
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("BungeeCord"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(!subchannel.equals("Forward"))
			return;
		short len = in.readShort();
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);

		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		String data;
		try {
			data = msgin.readUTF();
		} catch(IOException e) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Bungee-Spigot] The plugin received a plugin message from the Bungeecord instance. However, the data could not be read.");
			e.printStackTrace();
			return;
		}
		String[] fullData = data.split(" ");
		if(!fullData[0].equals("data_1VSGK")) {
			MsgUtils.sendBukkitConsoleMessage("&c[MatchaMC - Bungee-Spigot] The plugin received a plugin message from the Bungeecord instance. However, the data received does not match the format issued by the /sendtoall command.");
			return;
		}
		String[] modifiedFullData = Arrays.copyOfRange(fullData, 1, fullData.length);
		String receivedCommand = String.join(" ", modifiedFullData).trim();
		MsgUtils.sendBukkitConsoleMessage("&e[MatchaMC - Spigot] Sending command '" + receivedCommand + "' - Received from /sendtoall in BungeeCord network.");
		Bukkit.dispatchCommand(getServer().getConsoleSender(), receivedCommand);
	}
}
