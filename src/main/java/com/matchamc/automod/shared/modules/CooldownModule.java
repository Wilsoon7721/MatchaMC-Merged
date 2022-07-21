package com.matchamc.automod.shared.modules;

import java.util.HashMap;
import java.util.UUID;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;

public class CooldownModule implements Module {
	private HashMap<UUID, Integer> cooldown = new HashMap<>();

	private boolean enabled;
	private String bypassPermission = "automod.bypass.cooldown";

	public CooldownModule() {
		// set up cooldown stuff
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
		if(!cooldown.containsKey(player.getUniqueId()))
			return true; // TODO REMOVE PLAYER FROM COOLDOWN IF NO LONGER ON COOLDOWN
		return false;
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

	public void setCooldown(UUID uuid, int delaySeconds) {
		if(cooldown.containsKey(uuid))
			return;
		cooldown.put(uuid, delaySeconds);
	}

	public void remove(UUID uuid) {
		if(!cooldown.containsKey(uuid))
			return;
		cooldown.remove(uuid);
	}
}
