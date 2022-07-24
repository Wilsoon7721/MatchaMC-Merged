package com.matchamc.automod.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.matchamc.automod.shared.Module;
import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.Configurations;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.shared.MsgUtils;

public class AutoMod {
	private BukkitMain instance;
	private File config;
	private YamlConfiguration yc;
	private Staffs staffs;
	private PlayerRegistrar registrar;
	private Set<Module> activeModules = new HashSet<>();

	public AutoMod(BukkitMain instance, Configurations configurations, Staffs staffs, PlayerRegistrar registrar) {
		// TODO Spigot Sequence
		this.instance = instance;
		this.registrar = registrar;
		this.staffs = staffs;
		configurations = Objects.requireNonNull(configurations);
		config = new File(this.instance.getDataFolder(), "automod.yml");
		if(!config.exists())
			configurations.create("automod.yml");
		Bukkit.getScheduler().runTask(this.instance, () -> initializeModules());
	}

	public void initializeModules() {
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(config);
		// TODO get modules from it
		// chcek if eabled
		// load all enabled onnes
		// add to activeModules

	}

	public String getMessage(String key, String[][] placeholders) {
		// Key starts with messages.(key)
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(config);
		String msg = yc.getString("messages." + key);
		if(placeholders == null)
			return MsgUtils.color(msg);
		for(String[] placeholder : placeholders) {
			msg = msg.replace("%" + placeholder[0] + "%", placeholder[1]);
		}
		return MsgUtils.color(msg);
	}

	public YamlConfiguration getConfig() {
		yc = YamlConfiguration.loadConfiguration(config);
		return yc;
	}

	public void reloadConfig() {
		yc = YamlConfiguration.loadConfiguration(config);
		try {
			yc.save(config);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cAutoMod could not be reloaded.");
			ex.printStackTrace();
			return;
		}
		yc = YamlConfiguration.loadConfiguration(config);
	}

	public Set<Module> getActiveModules() {
		return activeModules;
	}

	public BukkitMain getBukkitInstance() {
		return instance;
	}

	public Staffs getStaffs() {
		return staffs;
	}

	public PlayerRegistrar getPlayerRegistrar() {
		return registrar;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getModuleAs(Module module, Class<T> clazz) throws ClassCastException {
		return (T) module;
	}
}
