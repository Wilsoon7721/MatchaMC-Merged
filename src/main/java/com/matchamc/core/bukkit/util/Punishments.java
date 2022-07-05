package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.matchamc.core.bukkit.BukkitMain;

public class Punishments {
	public static final String PLUGIN_PUNISHMENT_NAME = "MatchaMC Punishments (%executor%)";
	public static final UUID PLUGIN_PUNISHMENT_UUID = UUID.fromString("740c1ecf-8cde-4c24-8b02-9ffbc76ff7f2");
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private String permissionBan, permissionMute, permissionKick, permissionWarn;

	public Punishments(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		permissionBan = "staffcore.punish.ban";
		permissionMute = "staffcore.punish.mute";
		permissionKick = "staffcore.punish.kick";
		permissionWarn = "staffcore.punish.warn";
	}

	public Punishment createPunishment(Punishment.Type type, UUID executor, UUID punished, String reason, Duration duration) {
		return new Punishment(instance, registrar, type, executor, punished, reason, duration);
	}

	// GUIs
	public void openPunishmentGUI(Player player, UUID punished) {
		String punishedName = registrar.getNameFromRegistrar(punished);
		Inventory inv = Bukkit.createInventory(null, 9, "Issue Punishment: " + punishedName);
		if(player.hasPermission("staffcore."))
	}
}
