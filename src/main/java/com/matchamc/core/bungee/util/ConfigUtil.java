package com.matchamc.core.bungee.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.matchamc.core.bungee.BungeeMain;
import com.matchamc.shared.util.MsgUtils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigUtil {
	private Plugin plugin;

	public ConfigUtil(Plugin plugin) {
		this.plugin = plugin;
	}

	public Configuration get(String fileName) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), fileName));
		} catch(IOException ex) {
			ex.printStackTrace();
			MsgUtils.sendBungeeConsoleMessage("&c[" + plugin.getDescription().getName() + "] Failed to get file '" + fileName + "' from data folder.");
		}
		return null;
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
					MsgUtils.sendBungeeConsoleMessage("&a[" + BungeeMain.CONSOLE_PLUGIN_NAME + "] Successfully created file '" + file.getName() + "' ");
					return;
				} else {
					file.createNewFile();
				}
			}
		} catch(IOException ex) {
			MsgUtils.sendBungeeConsoleMessage("&c[" + BungeeMain.CONSOLE_PLUGIN_NAME + "] Failed to create configuration file.");
		}
	}
}
