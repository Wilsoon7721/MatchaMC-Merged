package com.matchamc.core.bukkit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.matchamc.core.bukkit.commands.ClearChatCmd;
import com.matchamc.core.bukkit.commands.FeedCmd;
import com.matchamc.core.bukkit.commands.GamemodeCmd;
import com.matchamc.core.bukkit.commands.GodCmd;
import com.matchamc.core.bukkit.commands.HealCmd;
import com.matchamc.core.bukkit.commands.MsgCmd;
import com.matchamc.core.bukkit.commands.MuteChatCmd;
import com.matchamc.core.bukkit.commands.NightVisionCmd;
import com.matchamc.core.bukkit.commands.OverrideCmd;
import com.matchamc.core.bukkit.commands.PlayerTimeCmd;
import com.matchamc.core.bukkit.commands.PlayerWeatherCmd;
import com.matchamc.core.bukkit.commands.ReplyCmd;
import com.matchamc.core.bukkit.commands.SkullCmd;
import com.matchamc.core.bukkit.commands.SpeedCmd;
import com.matchamc.core.bukkit.commands.TeleportCmd;
import com.matchamc.core.bukkit.commands.TeleportHereCmd;
import com.matchamc.core.bukkit.commands.TimeCmd;
import com.matchamc.core.bukkit.commands.TimezoneCmd;
import com.matchamc.core.bukkit.commands.TogglePMCmd;
import com.matchamc.core.bukkit.commands.WeatherCmd;
import com.matchamc.core.bukkit.commands.WhitelistCmd;
import com.matchamc.core.bukkit.commands.staff.NoteCmd;
import com.matchamc.core.bukkit.commands.staff.NotesCmd;
import com.matchamc.core.bukkit.commands.staff.SocialspyCmd;
import com.matchamc.core.bukkit.listeners.NotesGUIListener;
import com.matchamc.core.bukkit.util.AntiCheatDragbackHook;
import com.matchamc.core.bukkit.util.AntiCheatHook;
import com.matchamc.core.bukkit.util.AntiCheatTrigger;
import com.matchamc.core.bukkit.util.BanWave;
import com.matchamc.core.bukkit.util.Chat;
import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.core.bukkit.util.Messenger;
import com.matchamc.core.bukkit.util.Notes;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.ServerWhitelist;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.core.bukkit.util.TimeZones;
import com.matchamc.shared.MsgUtils;

public class BukkitMain extends JavaPlugin implements PluginMessageListener {
	public static final String CONSOLE_PLUGIN_NAME = "MatchaMC - Bukkit";
	public static String NON_PLAYER_ERROR, NO_PERMISSION_ERROR, INSUFFICIENT_PARAMETERS_ERROR, PLAYER_OFFLINE, INSUFFICIENT_INVENTORY_SPACE;
	private static Configurations configurations;
	private static YamlConfiguration messages;
	private static BukkitMain instance;
	public boolean bungee = false;
	private Staffs staffs;
	private BanWave banwave;
	private Chat chat = null;
	private PlayerRegistrar registrar;
	private TimeZones timezones;
	private Notes notes;
	private Messenger messenger;
	private ServerWhitelist whitelist;

	@Override
	public void onEnable() {
		// MsgUtils.sendBukkitConsoleMessage("&aEnabling MatchaMC [Bukkit/Spigot] version " + getDescription().getVersion());
		printIcon();
		checkBungee();
		saveDefaultConfig();
		reloadConfig();
		instance = this;
		configurations = new Configurations(this);
		staffs = new Staffs(this, configurations);
		registrar = new PlayerRegistrar(this, configurations, "staffcore.logininfo");
		whitelist = new ServerWhitelist(this, staffs);
		messenger = new Messenger(this);
		banwave = new BanWave(this, registrar, "staffcore.banwave");
		notes = new Notes(this);
		timezones = new TimeZones(this);
		configurations.create("messages.yml");
		Bukkit.getPluginManager().registerEvents(staffs, this);
		getCommand("clearchat").setExecutor(new ClearChatCmd(this, "core.clearchat"));
		registerCommandAndListener("mutechat", new MuteChatCmd(this, staffs, "core.mutechat"));
		getCommand("socialspy").setExecutor(new SocialspyCmd(this, messenger, "core.staff.socialspy"));
		getCommand("message").setExecutor(new MsgCmd(this, messenger, "core.message"));
		getCommand("nightvision").setExecutor(new NightVisionCmd(this, "core.nightvision"));
		getCommand("override").setExecutor(new OverrideCmd(this, whitelist));
		getCommand("ptime").setExecutor(new PlayerTimeCmd(this, "core.playertime"));
		getCommand("pweather").setExecutor(new PlayerWeatherCmd(this, "core.playerweather"));
		getCommand("reply").setExecutor(new ReplyCmd(this, messenger, "core.reply"));
		getCommand("heal").setExecutor(new HealCmd(this, "core.heal"));
		getCommand("feed").setExecutor(new FeedCmd(this, "core.feed"));
		getCommand("gamemode").setExecutor(new GamemodeCmd(this, "core.gamemode"));
		registerCommandAndListener("god", new GodCmd(this, "core.god"));
		registerCommandAndListener("logininfo", registrar);
		getCommand("speed").setExecutor(new SpeedCmd(this, "core.speed"));
		getCommand("skull").setExecutor(new SkullCmd(this, "core.skull"));
		getCommand("teleport").setExecutor(new TeleportCmd(this, "core.teleport"));
		getCommand("teleporthere").setExecutor(new TeleportHereCmd(this, "core.teleporthere"));
		getCommand("timezone").setExecutor(new TimezoneCmd(this, timezones, "core.timezone"));
		getCommand("time").setExecutor(new TimeCmd(this, "core.time"));
		getCommand("togglepm").setExecutor(new TogglePMCmd(this, messenger, "core.tpm"));
		getCommand("weather").setExecutor(new WeatherCmd(this, "core.weather"));
		registerCommandAndListener("whitelist", new WhitelistCmd(this, whitelist, "core.whitelist"));

		// STAFFCORE
		Bukkit.getPluginManager().registerEvents(new AntiCheatTrigger(), this);
		registerCommandAndListener("alerts", new AntiCheatHook(this, banwave, "staffcore.alerts"));
		getCommand("banwave").setExecutor(banwave);
		registerCommandAndListener("dbalerts", new AntiCheatDragbackHook(this, "staffcore.dbalerts"));
		getCommand("note").setExecutor(new NoteCmd(this, notes, "staffcore.note"));
		getCommand("notes").setExecutor(new NotesCmd(this, timezones, notes, "staffcore.notes"));
		Bukkit.getPluginManager().registerEvents(new NotesGUIListener(notes, timezones), this);
		reloadMessages();
	}

	private static void reloadMessages() {
		messages = configurations.get("messages.yml");
		NON_PLAYER_ERROR = messages.getString("non_player_error");
		NO_PERMISSION_ERROR = messages.getString("no_permission_error");
		INSUFFICIENT_PARAMETERS_ERROR = messages.getString("insufficient_parameters_error");
		INSUFFICIENT_INVENTORY_SPACE = messages.getString("insufficient_inventory_space");
		PLAYER_OFFLINE = messages.getString("player_offline");
	}

	public YamlConfiguration messages() {
		return messages;
	}

	public static void reloadData() {
		reloadMessages();
		instance.reloadConfig();
	}

	private void checkBungee() {
		if(!getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
			MsgUtils.sendBukkitConsoleMessage("&cThis server is not connected to BungeeCord.");
			MsgUtils.sendBukkitConsoleMessage("&cIf this is an error, please check your spigot.yml and change 'bungeecord' to true.");
			return;
		}
		getServer().getMessenger().registerOutgoingPluginChannel(this, "MatchaMC_ServerPlugin");
		getServer().getMessenger().registerIncomingPluginChannel(this, "MatchaMC_ServerPlugin", this);
		chat = new Chat(this, staffs, "core.bukkit.chat");
		registerCommandAndListener("chat", chat);
		bungee = true;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if(!channel.equals("MatchaMC_ServerPlugin"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if(subchannel.equals("Forward")) {
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
			String receivedCommand = String.join(" ", data).trim();
			MsgUtils.sendBukkitConsoleMessage("&e[MatchaMC - Spigot] Sending command &a" + receivedCommand + " &e- Received from /sendtoall in BungeeCord network.");
			Bukkit.dispatchCommand(getServer().getConsoleSender(), receivedCommand);
			return;
		}
	}

	public String formatNoPermsMsg(String permission) {
		return MsgUtils.color(NO_PERMISSION_ERROR.replace("%permission%", permission));
	}

	private <T> void registerCommandAndListener(String command, T object) {
		if(!(object instanceof CommandExecutor) || !(object instanceof Listener))
			throw new RuntimeException("registerCommandAndListener() refused the argument given which is of class: " + object.getClass().getCanonicalName() + ". Superclasses: " + object.getClass().getEnclosingClass().getCanonicalName() + ". The command and listeners attached to this class was not registered.");
		getCommand(command).setExecutor((CommandExecutor) object);
		Bukkit.getPluginManager().registerEvents((Listener) object, this);
	}

	private void printIcon() {
		// The escape codes messes up the perfect icon, but in the console it looks good - i think.
		String serverVersion = getServerVersion();
		MsgUtils.sendBukkitConsoleMessage("&a   __     ___     _____________       &3|");
		MsgUtils.sendBukkitConsoleMessage("&a  /  \\   /   \\   /   __________|    &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | /\\ \\_/ /\\  |  |  /              &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | | \\   /  | |  |  |                &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | |  | |   | |  |  |                 &3|    Running &bBukkit/Spigot v" + serverVersion);
		MsgUtils.sendBukkitConsoleMessage("&a | |  | |   | |  |  |                 &3|     &b" + getDescription().getName() + " &3version &b" + getDescription().getVersion());
		MsgUtils.sendBukkitConsoleMessage("&a | |  |_|   | |  |  |                 &3|         &aThe plugin is enabling...");
		MsgUtils.sendBukkitConsoleMessage("&a | |        | |  |  |                 &3|");
		MsgUtils.sendBukkitConsoleMessage("&a | |        | |  |  \\___________     &3|");
		MsgUtils.sendBukkitConsoleMessage("&a |_|        |_|  \\______________|    &3|");
	}

	private String getServerVersion() {
		if(Bukkit.getVersion().contains("1.8"))
			return "1.8";
		if(Bukkit.getVersion().contains("1.9"))
			return "1.9";
		if(Bukkit.getVersion().contains("1.10"))
			return "1.10";
		if(Bukkit.getVersion().contains("1.11"))
			return "1.11";
		if(Bukkit.getVersion().contains("1.12"))
			return "1.12";
		if(Bukkit.getVersion().contains("1.13"))
			return "1.13";
		if(Bukkit.getVersion().contains("1.14"))
			return "1.14";
		if(Bukkit.getVersion().contains("1.15"))
			return "1.15";
		if(Bukkit.getVersion().contains("1.16"))
			return "1.16";
		if(Bukkit.getVersion().contains("1.17"))
			return "1.17";
		if(Bukkit.getVersion().contains("1.18"))
			return "1.18";
		if(Bukkit.getVersion().contains("1.19"))
			return "1.19";
		if(Bukkit.getVersion().contains("1.20"))
			return "1.20";
		if(Bukkit.getVersion().contains("1.21"))
			return "1.21";
		return "[Unknown Version!]";
	}
}
