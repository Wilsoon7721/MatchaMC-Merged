package com.matchamc.core.bukkit.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Note {
	private BukkitMain instance;
	private Notes notes;
	private int id;
	private String creatorName, creatorUUID;
	private String content;
	private long createdOnMillis;
	private boolean deleted;

	public Note(Notes notes, Player creator, String content) {
		this.notes = notes;
		this.instance = this.notes.getPluginInstance();
		this.creatorName = creator.getName();
		this.creatorUUID = creator.getUniqueId().toString();
		this.content = content;
		createdOnMillis = System.currentTimeMillis();
		this.id = this.notes.getNextAvailableId();
		if(id == -1)
			return;
		instance.getConfig().set("notes." + id + ".id", id);
		instance.getConfig().set("notes." + id + ".content", this.content);
		instance.getConfig().set("notes." + id + ".created", createdOnMillis);
		instance.getConfig().set("notes." + id + ".creator.name", this.creatorName);
		instance.getConfig().set("notes." + id + ".creator.uuid", this.creatorUUID);
		instance.getConfig().set("notes." + id + ".deleted", false);
		instance.saveConfig();
		instance.reloadConfig();
		creator.sendMessage(MsgUtils.color("&eNote #" + id + " has been saved with content '" + this.content + "'"));
	}

	public Note(Notes notes, String creatorName, String creatorUUID, String content) {
		this.notes = notes;
		this.instance = this.notes.getPluginInstance();
		this.creatorName = creatorName;
		this.creatorUUID = creatorUUID;
		this.content = content;
		createdOnMillis = System.currentTimeMillis();
		id = this.notes.getNextAvailableId();
		if(id == -1)
			return;
		instance.getConfig().set("notes." + id + ".id", id);
		instance.getConfig().set("notes." + id + ".content", this.content);
		instance.getConfig().set("notes." + id + ".created", createdOnMillis);
		instance.getConfig().set("notes." + id + ".creator.name", this.creatorName);
		instance.getConfig().set("notes." + id + ".creator.uuid", this.creatorUUID);
		instance.getConfig().set("notes." + id + ".deleted", false);
		instance.saveConfig();
		instance.reloadConfig();
		Bukkit.getConsoleSender().sendMessage(MsgUtils.color("&eNote #" + id + " has been saved with content '" + this.content + "'"));
	}

	public Note(Notes notes, int id) {
		this.notes = notes;
		this.instance = this.notes.getPluginInstance();
		FileConfiguration cfg = instance.getConfig();
		this.id = cfg.getInt("notes." + id + ".id");
		this.content = cfg.getString("notes." + id + ".content");
		this.createdOnMillis = cfg.getLong("notes." + id + ".created");
		this.creatorName = cfg.getString("notes." + id + ".creator.name");
		this.creatorUUID = cfg.getString("notes." + id + ".creator.uuid");
		this.deleted = cfg.getBoolean("notes." + id + ".deleted");
	}

	public int getId() {
		return id;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public UUID getCreatorUUID() {
		return UUID.fromString(creatorUUID);
	}

	public String getContent() {
		return content;
	}

	public long getCreationTimeInMillis() {
		return createdOnMillis;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean delete() {
		this.deleted = true;
		FileConfiguration cfg = instance.getConfig();
		if(cfg.getBoolean("notes." + id + ".deleted"))
			return false;
		cfg.set("notes." + id + ".deleted", true);
		cfg.set("notes." + id + ".deletedtimestamp", System.currentTimeMillis());
		instance.saveConfig();
		instance.reloadConfig();
		return true;
	}
}
