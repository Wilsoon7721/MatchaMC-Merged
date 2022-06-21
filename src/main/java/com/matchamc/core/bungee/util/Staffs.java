package com.matchamc.core.bungee.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.shared.MsgUtils;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class Staffs implements Listener {
	private BungeeMain instance;
	private String permissionNode;
	private Set<UUID> staff;

	public Staffs(BungeeMain instance, String permissionNode) {
		this.instance = instance;
		this.permissionNode = permissionNode;
		File file = new File(instance.getDataFolder(), "staffs.yml");
		if(!file.exists()) {
			try { 
				file.createNewFile();
				instance.getProxy().getScheduler().schedule(this.instance, () -> {
					try {
						Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
						config.set("staffs", new ArrayList<>());
						ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
					} catch(IOException ex) {
						MsgUtils.sendBungeeConsoleMessage("&cStaffs first time setup failed - Could not create/load the staffs.yml file.");
						ex.printStackTrace();
						return;
					}
				}, 15, TimeUnit.MILLISECONDS);
			} catch(IOException ex) {
				MsgUtils.sendBungeeConsoleMessage("&cStaffs first time setup failed - Could not create/load the staffs.yml file.");
				ex.printStackTrace();
				return;
			}
		}
		Configuration config;
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch(IOException e) {
			MsgUtils.sendBungeeConsoleMessage("&cStaffs could not load the staffs.yml file.");
			e.printStackTrace();
			return;
		}
		staff.addAll(config.getStringList("staffs").stream().map(s -> UUID.fromString(s)).collect(Collectors.toList()));
	}

	public boolean isStaff(UUID uuid) {
		return staff.contains(uuid);
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		if(!event.getPlayer().hasPermission(permissionNode) || staff.contains(event.getPlayer().getUniqueId()))
			return;
		staff.add(event.getPlayer().getUniqueId());
	}

	public void commitNewStaffToFile() {
		File file = new File(instance.getDataFolder(), "staffs.yml");
		try {
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			config.set("staffs", staff.stream().map(UUID::toString).collect(Collectors.toList()));
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
		} catch(IOException e) {
			MsgUtils.sendBungeeConsoleMessage("&cStaffs could not load/save the staffs.yml file.");
			e.printStackTrace();
			return;
		}
	}
}
