package com.matchamc.automod.bukkit;

import java.util.UUID;

public class Violation {
	private UUID violator;
	private Module module;

	public Violation(UUID violator, Module module) {
		this.violator = violator;
		this.module = module;
	}

	public UUID getViolator() {
		return violator;
	}

	public Module getModule() {
		return module;
	}
}
