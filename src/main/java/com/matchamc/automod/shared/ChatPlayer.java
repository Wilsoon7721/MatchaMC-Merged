package com.matchamc.automod.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

public class ChatPlayer {
	private static Map<UUID, ChatPlayer> chatPlayers = new HashMap<>();
	private static Set<ChatPlayer> offlinePlayers = new HashSet<>();
	private UUID uuid;
	private Map<Module, Integer> warns;
	private boolean offline;
	private String[] lastMessages;
	private long lastMessageTime;

	private ChatPlayer(UUID uuid) {
		this.uuid = uuid;
		this.warns = new HashMap<>();
		this.lastMessages = new String[3];
		this.lastMessageTime = -1L;
	}

	public static ChatPlayer getChatPlayer(UUID uuid) {
		ChatPlayer chatPlayer = chatPlayers.getOrDefault(uuid, null);
		if(chatPlayer == null) {
			chatPlayer = new ChatPlayer(uuid);
			chatPlayers.put(uuid, chatPlayer);
		}
		return chatPlayer;
	}

	public void addWarn(Module module) {
		int warnings = warns.getOrDefault(module, 0);
		warns.put(module, warnings);
	}

	public int getWarns(Module module) {
		return warns.getOrDefault(module, 0);
	}

	public String[] getLastMessages() {
		return lastMessages;
	}

	public long getLastMessageTime() {
		return lastMessageTime;
	}

	public boolean isLastMessage(String msg) {
		if(msg == null)
			return false;
		byte b;
		int i;
		String[] arrayOfString;
		for(i = (arrayOfString = lastMessages).length, b = 0; b < i;) {
			String lastMessage = arrayOfString[b];
			if(msg.equals(lastMessage))
				return true;
			b++;
		}
		return false;
	}

	public void setLastMessage(String msg, long lastMessageTime) {
		msg = ChatColor.stripColor(msg);
		lastMessages[2] = lastMessages[1];
		lastMessages[1] = lastMessages[0];
		lastMessages[0] = msg;
		this.lastMessageTime = lastMessageTime;
	}

	public void clearWarnings() {
		this.warns.clear();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void setOffline() {
		if(offline) {
			if(!offlinePlayers.contains(this))
				offlinePlayers.add(this);
			return;
		}
		offline = true;
		offlinePlayers.add(this);
	}

	public void setOnline() {
		if(!offline) {
			if(offlinePlayers.contains(this))
				offlinePlayers.remove(this);
			return;
		}
		offline = false;
		offlinePlayers.remove(this);
	}
}
