package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.UUID;

import com.matchamc.core.bukkit.BukkitMain;

public class Punishments {
	private static final String PLUGIN_PUNISHMENT_NAME = "MatchaMC Punishments (%executor%)";
	private static final UUID PLUGIN_PUNISHMENT_UUID = UUID.fromString("740c1ecf-8cde-4c24-8b02-9ffbc76ff7f2");
	private BukkitMain instance;
	public Punishments(BukkitMain instance) {
		this.instance = instance;
	}
	
	public Punishment createPunishment(Punishment.Type type, UUID executor, UUID punished, String reason, Duration duration) {
		return new Punishment(type, executor, punished, reason, duration);
	}
}
