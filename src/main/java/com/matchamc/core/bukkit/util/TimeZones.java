package com.matchamc.core.bukkit.util;

import java.time.ZoneOffset;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TimeZones {
	private BukkitMain instance;

	public TimeZones(BukkitMain instance) {
		this.instance = instance;
	}

	public void addEntry(UUID uuid, ZoneOffset zoneOffset) {
		FileConfiguration config = instance.getConfig();
		config.set("playerTimeZones." + uuid.toString(), zoneOffset.toString());
		instance.reloadConfig();
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
			player.spigot().sendMessage(new ComponentBuilder("You have set your timezone to ").color(ChatColor.YELLOW).append(new ComponentBuilder(zoneOffset.toString()).color(ChatColor.GREEN).append(new ComponentBuilder(".").color(ChatColor.YELLOW).create()).create()).create());
	}

	public ZoneOffset getEntry(UUID uuid) {
		FileConfiguration config = instance.getConfig();
		String s = config.getString("playerTimeZones." + uuid.toString());
		if(s == null || s.isBlank())
			return ZoneOffset.of("UTC");
		return ZoneOffset.of(s);
	}

	public boolean hasEntry(UUID uuid) {
		FileConfiguration config = instance.getConfig();
		String s = config.getString("playerTimeZones." + uuid.toString());
		if(s == null || s.isBlank())
			return false;
		return true;
	}

	public void removeEntry(UUID uuid) {
		FileConfiguration config = instance.getConfig();
		config.set("playerTimeZones." + uuid.toString(), null);
		instance.saveConfig();
		instance.reloadConfig();
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
			player.spigot().sendMessage(new ComponentBuilder("You have reset your timezone to ").color(ChatColor.YELLOW).append(new ComponentBuilder("UTC").color(ChatColor.GREEN).append(new ComponentBuilder(".").color(ChatColor.YELLOW).create()).create()).create());
	}
}
