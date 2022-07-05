package com.matchamc.core.bukkit.util;

import java.time.Duration;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

import litebans.api.Database;

public class Punishment {
	public static boolean cancel = false;
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private Type type;
	private UUID executor, punished;
	private String reason;
	private Duration duration;
	private boolean executed = false;

	public Punishment(BukkitMain instance, PlayerRegistrar registrar, Type type, UUID executor, UUID punished, String reason, Duration duration) {
		// specify null duration for forever
		this.instance = instance;
		this.registrar = registrar;
		this.type = type;
		this.executor = executor;
		this.punished = punished;
		this.reason = reason;
		this.duration = duration;
	}

	public void executePunishment() {
		Database database = Database.get();
		String additional_arguments = "--sender=" + (Punishments.PLUGIN_PUNISHMENT_NAME.replace("%executor%", registrar.getNameFromRegistrar(executor)) + " --sender-uuid=" + executor.toString());
		switch(type) {
		case BAN:
			new BukkitRunnable() {
				@Override
				public void run() {
					Punishment.cancel = database.isPlayerBanned(punished, null);
				}
			}.runTaskAsynchronously(instance);
			if(Punishment.cancel) {
				Bukkit.getPlayer(executor).sendMessage(MsgUtils.color("&cThis player is already banned."));
				Punishment.cancel = false;
				return;
			}
			if(duration == null)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + punished + "-s " + reason + " " + additional_arguments);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + punished + "-s " + duration.toDays() + "d " + reason + " " + additional_arguments);
			executed = true;
			break;
		case MUTE:
			new BukkitRunnable() {
				@Override
				public void run() {
					Punishment.cancel = database.isPlayerMuted(punished, null);
				}
			}.runTaskAsynchronously(instance);
			if(Punishment.cancel) {
				Bukkit.getPlayer(executor).sendMessage(MsgUtils.color("&cThis player is already muted."));
				Punishment.cancel = false;
				return;
			}
			if(duration == null)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + punished + "-s " + reason + " " + additional_arguments);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + punished + "-s " + duration.toDays() + "d " + reason + " " + additional_arguments);
			executed = true;
			break;
		case WARN:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn -s " + punished + " " + reason + " " + additional_arguments);
			executed = true;
			break;
		case KICK:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick -s " + punished + " " + reason + " " + additional_arguments);
			executed = true;
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

	public boolean isExecuted() {
		return executed;
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
