package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.UUID;

public class Punishment {
	private Type type;
	private UUID executor, punished;
	private String reason;
	private Duration duration;

	public Punishment(Type type, UUID executor, UUID punished, String reason, Duration duration) {
		// specify null duration for forever
		this.type = type;
		this.executor = executor;
		this.punished = punished;
		this.reason = reason;
		this.duration = duration;
	}

	public void executePunishment() {
		switch(type) {
		case BAN:
			break;
		case MUTE:
			break;
		case WARN:
			break;
		case KICK:
			break;
		}
	}

	public Type getPunishmentType() {
		return type;
	}

	public UUID getExecutor() {
		return executor;
	}

	public UUID getPunished() {
		return punished;
	}

	public String getReason() {
		return reason;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setExecutor(UUID executor) {
		this.executor = executor;
	}

	public void setPunished(UUID punished) {
		this.punished = punished;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public static enum Type {
		BAN, MUTE, KICK, WARN;
	}
}
