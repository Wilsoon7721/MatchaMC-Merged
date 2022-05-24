package com.matchamc.shared.util;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class MsgUtils {
	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void sendConsoleMessage(String s) {
		Bukkit.getConsoleSender().sendMessage(color(s));
	}
}
