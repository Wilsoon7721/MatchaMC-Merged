package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MathUtil;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.ChatColor;

public class PlayerRegistrar extends CoreCommand implements Listener {
	private BukkitMain instance;
	private File file;
	private DateTimeFormatter formatter;
	private Map<UUID, Integer> runnableIds = new HashMap<>();

	public PlayerRegistrar(BukkitMain instance, Configurations configurations, String permissionNode) {
		super(instance, permissionNode);
		file = configurations.getFile("players.yml");
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:MM:ss").withZone(ZoneId.of("UTC"));
		if(!(configurations.exists("players.yml"))) {
			configurations.plainCreate("players.yml");
			Bukkit.getScheduler().runTask(this.instance, () -> {
				YamlConfiguration yc = configurations.get("players.yml");
				yc.createSection("players");
				try {
					yc.save(file);
				} catch(IOException ex) {
					MsgUtils.sendBukkitConsoleMessage("&cFailed to write to player database file.");
					ex.printStackTrace();
					return;
				}
				YamlConfiguration.loadConfiguration(file);
			});
		}
	}

	public void fetchPlayerHistory(CommandSender sender, UUID uuid, int page) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		List<String> history = yc.getStringList("players." + uuid + ".login-history").stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toUnmodifiableList());
		if(history.isEmpty()) {
			sender.sendMessage(MsgUtils.color("&cNo login/logout records found for this player."));
			return;
		}
		List<List<String>> parts = MathUtil.separateList(history, 8);
		if(page > parts.size()) {
			sender.sendMessage(MsgUtils.color("&cThere is no page " + page + " for this player."));
			return;
		}
		List<String> specificPage = parts.get((page - 1));
		sender.sendMessage(MsgUtils.color("&e--- Records " + page + "/" + parts.size() + " ---"));
		for(String s : specificPage) {
			sender.sendMessage(s);
		}
	}

	public boolean isRegistered(UUID uuid) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		if(yc.getConfigurationSection("players." + uuid.toString()) != null)
			return true;
		return false;
	}

	public UUID resolveUUIDFromName(String name) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		for(String uuidKey : yc.getConfigurationSection("players").getKeys(false)) {
			String nameValue = yc.getString("players." + uuidKey + ".name");
			if(nameValue.isBlank())
				continue;
			if(!nameValue.equalsIgnoreCase(name))
				continue;
			return UUID.fromString(uuidKey);
		}
		return null;
	}

	public String getNameFromRegistrar(UUID uuid) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		String suuid = uuid.toString();
		String name = yc.getString("players." + suuid + ".name");
		return name;
	}

	public Collection<UUID> getAllUUIDsMatchingName(String query, boolean exactMatch) {
		Collection<UUID> uuids = new HashSet<>();
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		for(String key : yc.getConfigurationSection("players").getKeys(false)) {
			if(exactMatch) {
				if(yc.getString("players." + key + ".name").equalsIgnoreCase(query))
					uuids.add(UUID.fromString(key));
			} else {
				String s = yc.getString("players." + key + ".name");
				if(s.toLowerCase().contains(query.toLowerCase()))
					uuids.add(UUID.fromString(key));
			}
		}
		return uuids;
	}

	public void registerPlayer(Player player) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		String uuid = player.getUniqueId().toString();
		yc.set("players." + uuid + ".name", player.getName());
		yc.set("players." + uuid + ".local-times-logged-in", 1); // How many times a player logs into the CURRENT server (Could be one of the spigot servers on the network)
		yc.set("players." + uuid + ".firstjoined", System.currentTimeMillis());
		yc.set("players." + uuid + ".login-history", new ArrayList<>());
		try {
			yc.save(file);
		} catch(IOException ex) {}
		YamlConfiguration.loadConfiguration(file);
	}

	public Collection<String> getAllRegisteredPlayerNames() {
		Set<String> c = new HashSet<>();
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		for(String key : yc.getConfigurationSection("players").getKeys(false)) {
			String s = yc.getString("players." + key + ".name");
			c.add(s);
		}
		return c;
	}

	@EventHandler
	public void onJoinRegister(PlayerJoinEvent event) {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		if(isRegistered(event.getPlayer().getUniqueId())) {
			int count = yc.getInt("players." + event.getPlayer().getUniqueId().toString() + ".local-times-logged-in");
			yc.set("players." + event.getPlayer().getUniqueId().toString() + ".name", event.getPlayer().getName()); // Update name changes
			yc.set("players." + event.getPlayer().getUniqueId().toString() + ".local-times-logged-in", (count + 1));
			try {
				yc.save(file);
			} catch(IOException ex) {}
			YamlConfiguration.loadConfiguration(file);
			return;
		}
		registerPlayer(event.getPlayer());
	}

	@EventHandler
	public void onJoinModifyHistory(PlayerJoinEvent event) {
		long loginTime = System.currentTimeMillis();
		UUID uuid = event.getPlayer().getUniqueId();
		if(runnableIds.get(uuid) != null) {
			int id = runnableIds.get(uuid);
			Bukkit.getScheduler().cancelTask(id);
			runnableIds.remove(uuid);
		}
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
			if(Bukkit.getPlayer(uuid) == null)
				return;
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
			List<String> history = yc.getStringList("players." + uuid + ".login-history");
			Instant instant = Instant.ofEpochMilli(loginTime);
			String loginData = "&8[&e" + formatter.format(instant) + "&8] &aPlayer logged in";
			history.add(loginData);
			yc.set("players." + uuid + ".login-history", history);
			try {
				yc.save(file);
			} catch(IOException ex) {
				MsgUtils.sendBukkitConsoleMessage("&c[PlayerHistory] Failed to save login data.");
				ex.printStackTrace();
				return;
			}
			YamlConfiguration.loadConfiguration(file);
			MsgUtils.sendBukkitConsoleMessage("&a[PlayerHistory] Registered login for " + event.getPlayer().getName());
		}, 1200L);
		runnableIds.put(uuid, taskId);
	}

	@EventHandler
	public void onLeaveModifyHistory(PlayerQuitEvent event) {
		long logoutTime = System.currentTimeMillis();
		UUID uuid = event.getPlayer().getUniqueId();
		if(runnableIds.get(uuid) != null) {
			int taskId = runnableIds.get(uuid);
			Bukkit.getScheduler().cancelTask(taskId);
			MsgUtils.sendBukkitConsoleMessage("&c[PlayerHistory] " + event.getPlayer().getName() + " left before the 60 second timer was up, so the login and logout was not recorded.");
			runnableIds.remove(uuid);
			return;
		}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		List<String> history = yc.getStringList("players." + uuid + ".login-history");
		Instant instant = Instant.ofEpochMilli(logoutTime);
		String logoutData = "&8[&e" + formatter.format(instant) + "&8] &cPlayer logged out";
		history.add(logoutData);
		yc.set("players." + uuid + ".login-history", history);
		try {
			yc.save(file);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[PlayerHistory] Failed to save logout data.");
			ex.printStackTrace();
			return;
		}
		YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// StaffCore module
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /logininfo <name/uuid>"));
			return true;
		}
		UUID uuid;
		try {
			uuid = UUID.fromString(args[0]);
		} catch(IllegalArgumentException ex) {
			// parse as name
			@SuppressWarnings("deprecation")
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
			uuid = op.getUniqueId();
		}
		if(args.length == 1) {
			fetchPlayerHistory(sender, uuid, 1);
			return true;
		}
		Integer page;
		try {
			page = Integer.parseInt(args[1]);
		} catch(NumberFormatException ex) {
			sender.sendMessage(MsgUtils.color("&cYou must provide a valid number as the page!"));
			return true;
		}
		fetchPlayerHistory(sender, uuid, page);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}
}
