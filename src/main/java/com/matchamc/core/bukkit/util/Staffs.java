package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.util.MsgUtils;

public class Staffs {
	private BukkitMain instance;
	private Configurations configurations;
	private Set<UUID> staff;
	private File staffFile;
	public Staffs(BukkitMain instance, Configurations configurations) {
		this.instance = instance;
		this.configurations = configurations;
		staffFile = new File(this.instance.getDataFolder(), "staffs.yml");
		if(!(staffFile.exists())) {
			this.configurations.plainCreate("staffs.yml");
			YamlConfiguration yc = YamlConfiguration.loadConfiguration(staffFile);
			yc.createSection("staff");
			try {
				yc.save(staffFile);
				YamlConfiguration.loadConfiguration(staffFile);
			} catch(IOException ex) {
				MsgUtils.sendBukkitConsoleMessage("&c[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Could not load Staffs: The YML file (staffs.yml) could not be saved.");
				ex.printStackTrace();
				return;
			}
		}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(staffFile);
		staff.addAll(yc.getStringList("staff").stream().map(s -> UUID.fromString(s)).collect(Collectors.toList()));
	}
	
	public boolean addPlayer(Player player) {
		boolean success = staff.add(player.getUniqueId());
		return success;
	}

	public boolean addPlayer(UUID uuid) {
		boolean success = staff.add(uuid);
		return success;
	}

	public boolean addPlayer(OfflinePlayer offlinePlayer) {
		boolean success = staff.add(offlinePlayer.getUniqueId());
		return success;
	}

	public boolean removePlayer(Player player) {
		boolean success = staff.remove(player.getUniqueId());
		return success;

	}

	public boolean removePlayer(UUID uuid) {
		boolean success = staff.remove(uuid);
		return success;
	}

	public boolean removePlayer(OfflinePlayer offlinePlayer) {
		boolean success = staff.remove(offlinePlayer.getUniqueId());
		return success;
	}

	public void saveToFile() {
		List<String> uuids = staff.stream().map(UUID::toString).collect(Collectors.toList());
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(staffFile);
		yc.set("staff", uuids);
		try {
			yc.save(staffFile);
			YamlConfiguration.loadConfiguration(staffFile);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Could not save Staffs: The YML file (staffs.yml) could not be saved.");
			ex.printStackTrace();
		}
	}
}
