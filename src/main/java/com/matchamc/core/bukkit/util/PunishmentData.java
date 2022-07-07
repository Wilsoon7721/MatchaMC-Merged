package com.matchamc.core.bukkit.util;

import java.util.UUID;

public class PunishmentData {
	private UUID executor, punished;

	protected PunishmentData(UUID executor, UUID punished) {
		this.executor = executor;
		this.punished = punished;
	}

	public UUID getPunishmentExecutor() {
		return executor;
	}

	public UUID getPunished() {
		return punished;
	}
}
