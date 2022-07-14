package com.matchamc.automod.bukkit;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.shared.MsgUtils;

public class AutoMod {
	private BukkitMain instance;
	private File config;
	private Set<Module> activeModules = new HashSet<>();

	public AutoMod(BukkitMain instance, Configurations configurations) {
		// TODO Spigot Sequence
		this.instance = instance;
		configurations = Objects.requireNonNull(configurations);
		config = new File(this.instance.getDataFolder(), "automod.yml");
		if(!config.exists())
			configurations.create("automod.yml");
		Bukkit.getScheduler().runTask(this.instance, () -> initializeModules());
	}

	public void initializeModules() {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(config);
		// get modules from it
		// chcek if eabled
		// load all enabled onnes
		// add to activeModules

	}

	public Set<Module> getActiveModules() {
		return activeModules;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getModuleAs(Module module, Class<T> clazz, boolean silent) {
		if(module.getClass().isAssignableFrom(clazz))
			return (T) module;
		if(!silent)
			MsgUtils.sendBukkitConsoleMessage("&c -- AutoMod: Could not get Module as " + clazz.getCanonicalName());
		return null;
	}
}
