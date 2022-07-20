package com.matchamc.automod.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.matchamc.automod.shared.ChatPlayer;
import com.matchamc.automod.shared.Module;
import com.matchamc.automod.shared.modules.CapsModule;

import net.md_5.bungee.api.ChatColor;

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
		if(event.getPlayer().hasPermission(capsModule.getBypassPermission()))
			return;
		ChatPlayer chatPlayer = ChatPlayer.getChatPlayer(event.getPlayer().getUniqueId());
		String msg = ChatColor.stripColor(event.getMessage());
		if(!capsModule.meetsCondition(chatPlayer, msg))
			return;
		event.setCancelled(true);
		// TODO
		// check if CapsModule#isReplace active
		if(capsModule.isReplace()) {
			String msg = 
		}
		// send warning message
		// resend message after replacing
	}
}
