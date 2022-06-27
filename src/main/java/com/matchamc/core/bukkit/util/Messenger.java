package com.matchamc.core.bukkit.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Messenger {
	private BukkitMain instance;
	private Set<UUID> privateMessagesDisabled = new HashSet<>();
	private String sendingFormat, receivingFormat, socialSpyFormat;
	private Set<UUID> socialspyEnabled = new HashSet<>();
	private Map<UUID, UUID> lastMessagedPlayers = new HashMap<>();

	public Messenger(BukkitMain instance) {
		this.instance = instance;
		this.sendingFormat = this.instance.messages().getString("commands.messaging.format.sending");
		this.receivingFormat = this.instance.messages().getString("commands.messaging.format.receiving");
		this.socialSpyFormat = this.instance.messages().getString("commands.socialspy.format");
	}

	public void sendMessage(CommandSender fromPlayer, Player toPlayer, String message) {
		// Send message to fromPlayer with the 'To (Player): (message)' format
		// Send message to toPlayer with the 'From (Player): (message)' format
		fromPlayer.sendMessage(MsgUtils.color(sendingFormat.replace("%player%", toPlayer.getDisplayName()).replace("%message%", message)));
		toPlayer.sendMessage(MsgUtils.color(receivingFormat.replace("%player%", fromPlayer.getName()).replace("%message%", message)));
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!getSocialspyState(player))
				continue;
			player.sendMessage(MsgUtils.color(socialSpyFormat.replace("%sender%", fromPlayer.getName()).replace("%receiver%", toPlayer.getDisplayName()).replace("%message%", message)));
		}
	}

	public UUID getLastMessagedPlayer(Player player) {
		return lastMessagedPlayers.get(player.getUniqueId());
	}

	public void setLastMessagedPlayer(Player player, Player target) {
		if(lastMessagedPlayers.get(player.getUniqueId()) != null)
			lastMessagedPlayers.remove(player.getUniqueId());
		if(target == null) {
			lastMessagedPlayers.remove(player.getUniqueId());
			return;
		}
		lastMessagedPlayers.put(player.getUniqueId(), target.getUniqueId());
	}

	public boolean getSocialspyState(Player player) {
		return socialspyEnabled.contains(player.getUniqueId());
	}

	public boolean setSocialspyState(Player player, boolean state) {
		if(socialspyEnabled.contains(player.getUniqueId()) && !state) {
			socialspyEnabled.remove(player.getUniqueId());
			return state;
		}
		if(!socialspyEnabled.contains(player.getUniqueId()) && state) {
			socialspyEnabled.add(player.getUniqueId());
			return state;
		}
		return state;
	}

	public boolean isPMDisabled(UUID uuid) {
		return privateMessagesDisabled.contains(uuid);
	}

	public void setPMDisabled(UUID uuid, boolean state) {
		if(state) {
			privateMessagesDisabled.add(uuid);
			return;
		}
		privateMessagesDisabled.remove(uuid);
	}
}
