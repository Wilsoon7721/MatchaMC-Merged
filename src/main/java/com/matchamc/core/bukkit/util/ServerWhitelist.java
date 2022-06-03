package com.matchamc.core.bukkit.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;
import com.matchamc.shared.Staffs;

public class ServerWhitelist {
	private BukkitMain instance;
	private Staffs staffs;
	private Set<UUID> whitelistedUUIDs = new HashSet<>();
	private boolean enabled;

	public ServerWhitelist(BukkitMain instance, Staffs staffs) {
		this.instance = instance;
		this.staffs = staffs;
		this.enabled = this.instance.getConfig().getBoolean("whitelist.active");
		whitelistedUUIDs.addAll(instance.getConfig().getStringList("whitelist.players").stream().map(s -> UUID.fromString(s)).collect(Collectors.toSet()));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean state) {
		enabled = state;
	}

	public boolean isWhitelisted(OfflinePlayer player) {
		return whitelistedUUIDs.contains(player.getUniqueId());
	}

	public boolean addPlayer(Player player) {
		return whitelistedUUIDs.add(player.getUniqueId());
	}

	public boolean addPlayer(OfflinePlayer player) {
		return whitelistedUUIDs.add(player.getUniqueId());
	}

	public boolean addPlayer(UUID uuid) {
		return whitelistedUUIDs.add(uuid);
	}

	public boolean removePlayer(Player player) {
		return whitelistedUUIDs.remove(player.getUniqueId());
	}

	public boolean removePlayer(OfflinePlayer player) {
		return whitelistedUUIDs.remove(player.getUniqueId());
	}

	public boolean removePlayer(UUID uuid) {
		return whitelistedUUIDs.remove(uuid);
	}

	public boolean removeAll(Collection<?> collection) {
		return this.whitelistedUUIDs.removeAll(collection);
	}

	public boolean addAll(Collection<? extends UUID> collection) {
		return this.whitelistedUUIDs.addAll(collection);
	}

	public Set<UUID> getWhitelistedPlayers() {
		return whitelistedUUIDs;
	}

	public void refreshWhitelist() {
		// Kick all players that are not on the whitelist. If the server is set to whitelisted, non-whitelisted players can join back.
		saveToFile();
		Set<UUID> whitelisted = instance.getConfig().getStringList("whitelist.players").stream().map(s -> UUID.fromString(s)).collect(Collectors.toSet());
		Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).filter(uuid -> !whitelisted.contains(uuid)).map(Bukkit::getPlayer).forEach(player -> player.kickPlayer(MsgUtils.color("&cKicked: The server has been whitelisted, and you are not permitted to join.")));
	}

	public void whitelistAllStaff() {
		whitelistedUUIDs.addAll(staffs.getAllStaff());
		saveToFile();
	}

	public void clearWhitelist() {
		whitelistedUUIDs.clear();
		saveToFile();
	}

	public void saveToFile() {
		instance.getConfig().set("whitelist.active", enabled);
		instance.getConfig().set("whitelist.players", whitelistedUUIDs.stream().map(UUID::toString).collect(Collectors.toList()));
		instance.saveConfig();
		instance.reloadConfig();
	}
}
