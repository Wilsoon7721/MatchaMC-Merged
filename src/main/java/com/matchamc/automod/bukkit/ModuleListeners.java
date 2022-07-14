package com.matchamc.automod.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ModuleListeners implements Listener {
	private AutoMod autoMod;

	protected ModuleListeners(AutoMod autoMod) {
		this.autoMod = autoMod;
	}

	// CapsModule
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCapsChat(AsyncPlayerChatEvent event) {
		if(autoMod.getActiveModules())
	}
}
