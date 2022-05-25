package com.matchamc.core.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.matchamc.shared.util.MsgUtils;

public class BukkitMain extends JavaPlugin {
	@Override
	public void onEnable() {
		MsgUtils.sendConsoleMessage("&aEnabling MatchaMC [Bukkit/Spigot] version " + getDescription().getVersion());
	}
}
