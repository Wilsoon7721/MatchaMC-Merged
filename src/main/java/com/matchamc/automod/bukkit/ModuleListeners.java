package com.matchamc.automod.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.matchamc.automod.shared.Module;
import com.matchamc.automod.shared.modules.CapsModule;

public class ModuleListeners implements Listener {
	private AutoMod autoMod;
	private CapsModule capsModule;

	protected ModuleListeners(AutoMod autoMod) {
		this.autoMod = autoMod;
		for(Module module : autoMod.getActiveModules()) {
			try {
				capsModule = AutoMod.getModuleAs(module, CapsModule.class);
			} catch(ClassCastException ex) {
			}
		}

	}

	// CapsModule
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCapsChat(AsyncPlayerChatEvent event) {
		if(capsModule == null)
			return;
		if(autoMod.getStaffs().isStaff(event.getPlayer().getUniqueId()))
			return;

	}
}
