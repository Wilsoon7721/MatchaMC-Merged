package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.util.MsgUtils;

public class Configurations {
	private JavaPlugin plugin;

	public Configurations(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public YamlConfiguration get(String fileName) {
		File file = new File(plugin.getDataFolder(), fileName);
		if(!file.exists())
			create(fileName);
		return YamlConfiguration.loadConfiguration(file);
	}

	// Generate file by copying file from class loader, if the class loader does not contain a copy, then create the file.
	public void create(String fileName) {
		try {
			File file = new File(plugin.getDataFolder(), fileName);
			if(!file.exists()) {
				String[] files = fileName.split("/");
				InputStream stream = this.plugin.getClass().getClassLoader().getResourceAsStream(files[files.length - 1]);
				File parentFile = file.getParentFile();
				if(parentFile != null)
					parentFile.mkdirs();
				if(stream != null) {
					Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
					MsgUtils.sendBukkitConsoleMessage("&a[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Successfully copied the file '" + file.getName() + "' from the JAR to the data folder.");
					return;
				} else {
					file.createNewFile();
					MsgUtils.sendBukkitConsoleMessage("&a[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Successfully created the file '" + file.getName() + "'.");
				}
			}
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&c[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Failed to create configuration file.");
			ex.printStackTrace();
		}
	}

	public void plainCreate(String fileName) {
		File file = new File(plugin.getDataFolder(), fileName);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch(IOException ex) {
				MsgUtils.sendBukkitConsoleMessage("&c[" + BukkitMain.CONSOLE_PLUGIN_NAME + "] Failed to create configuration file.");
				ex.printStackTrace();
				return;
			}
	}
}
