package com.matchamc.automod.shared.modules;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.matchamc.automod.bukkit.AutoMod;
import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;
import com.matchamc.core.bukkit.util.Chat;

public class CooldownModule implements Module {
	private AutoMod autoMod;
	private HashMap<UUID, Integer> cooldown = new HashMap<>();
	private boolean enabled;
	private String bypassPermission = "automod.bypass.cooldown";
	private int cooldownTime;

	public CooldownModule(AutoMod autoMod) {
		this.autoMod = autoMod;
	}

	public void loadModule(int cooldownTime) {
		this.cooldownTime = cooldownTime;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(cooldown.isEmpty())
					return;
				for(Entry<UUID, Integer> entry : cooldown.entrySet()) {
					int value = entry.getValue();
					if(value <= 0) {
						cooldown.remove(entry.getKey());
						continue;
					}
					int newValue = (value - 1);
					cooldown.put(entry.getKey(), newValue);
					if(newValue <= 0) {
						cooldown.remove(entry.getKey());
						continue;
					}
					continue;
				}
			}
		}.runTaskTimerAsynchronously(autoMod.getBukkitInstance(), 1L, 20L);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isBypassable() {
		return true;
	}

	@Override
	public String getBypassPermission() {
		return bypassPermission;
	}

	// meetsCondition should be true if player can chat, and false if player on cooldown
	@Override
	public boolean meetsCondition(ChatPlayer player, String message) {
		if(!this.enabled)
			return true;
		if(autoMod.getBukkitInstance().getChat().getPlayerChannel(player.toBukkitPlayer()) == Chat.Channel.ALL && isOnCooldown(player.getUniqueId()))
			return false; // TODO REMOVE PLAYER FROM COOLDOWN IF NO LONGER ON COOLDOWN
		return true;
	}

	// no warnings if cant chat
	@Override
	public int getMaxWarnings() {
		return -1;
	}

	@Override
	public String getModuleName() {
		return "Cooldown";
	}

	public boolean isOnCooldown(UUID uuid) {
		return cooldown.containsKey(uuid);
	}

	public int getCooldown(UUID uuid) {
		if(!cooldown.containsKey(uuid))
			return 0;
		return cooldown.get(uuid);
	}

	public void setCooldown(UUID uuid) {
		if(cooldown.containsKey(uuid))
			return;
		cooldown.put(uuid, cooldownTime);
	}

	public void remove(UUID uuid) {
		if(!cooldown.containsKey(uuid))
			return;
		cooldown.remove(uuid);
	}
}
