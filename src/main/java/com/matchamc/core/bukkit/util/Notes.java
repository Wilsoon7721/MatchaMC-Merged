package com.matchamc.core.bukkit.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Notes {
	private BukkitMain instance;

	public Notes(BukkitMain instance) {
		this.instance = instance;
	}

	public void createNote(Player creator, String noteContent) {
		int id = getNextAvailableId();
		if(id == -1) {
			creator.sendMessage(MsgUtils.color("&cFailed to create note: The note ID is invalid."));
			return;
		}
		instance.getConfig().set("notes." + id + ".id", id);
		instance.getConfig().set("notes." + id + ".content", noteContent);
		instance.getConfig().set("notes." + id + ".created", System.currentTimeMillis());
		instance.getConfig().set("notes." + id + ".creator.name", creator.getName());
		instance.getConfig().set("notes." + id + ".creator.uuid", creator.getUniqueId().toString());
		instance.saveConfig();
		instance.reloadConfig();
		creator.sendMessage(MsgUtils.color("&eNote #" + id + " has been saved with content '" + noteContent + "'"));
	}

	public void createNote(String creatorName, String noteContent) {
		int id = getNextAvailableId();
		if(id == -1) return;
		instance.getConfig().set("notes." + id + ".id", id);
		instance.getConfig().set("notes." + id + ".content", noteContent);
		instance.getConfig().set("notes." + id + ".created", System.currentTimeMillis());
		instance.getConfig().set("notes." + id + ".creator.name", creatorName);
		instance.getConfig().set("notes." + id + ".creator.uuid", "");
		instance.saveConfig();
		instance.reloadConfig();
		Bukkit.getConsoleSender().sendMessage(MsgUtils.color("&eNote #" + id + " has been saved with content '" + noteContent + "'"));
	}

	public void deleteNote(CommandSender sender, int id) {
		instance.getConfig().set("notes." + id, null);
		instance.saveConfig();
		instance.reloadConfig();
		sender.sendMessage(MsgUtils.color("&eNote #" + id + " has been deleted."));
	}

	public int getNextAvailableId() {
		try {
			List<Integer> keys = instance.getConfig().getConfigurationSection("notes").getKeys(false).stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
			Integer i = Collections.max(keys);
			return (i + 1);
		} catch(NumberFormatException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cFailed to get next available id from Notes.");
			MsgUtils.sendBukkitConsoleMessage("&cConfiguration error - A Note ID is unable to parse through Integer#parseInt.");
			return -1;
		}
	}

	public BukkitMain getPluginInstance() {
		return instance;
	}
}
