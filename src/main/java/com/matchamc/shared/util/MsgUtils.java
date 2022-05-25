package com.matchamc.shared.util;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class MsgUtils {
	public static String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void sendBukkitConsoleMessage(String s) {
		Bukkit.getConsoleSender().sendMessage(color(s));
	}

	public static void sendBungeeConsoleMessage(String s) {
		ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(color(s)));
	}
}
