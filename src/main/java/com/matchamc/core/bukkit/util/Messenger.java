package com.matchamc.core.bukkit.util;

import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;

public class Messenger {
	private BukkitMain instance;

	public Messenger(BukkitMain instance) {
		this.instance = instance;
	}
	
	public void sendMessage(Player fromPlayer, Player toPlayer, String message) {
		// Get the format
		// Send message to fromPlayer with the 'To (Player): (message)' format
		// Send message to toPlayer with the 'From (Player): (message)' format
	}
}
