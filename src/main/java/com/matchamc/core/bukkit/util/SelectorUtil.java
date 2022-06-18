package com.matchamc.core.bukkit.util;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SelectorUtil {

	private SelectorUtil() {}

	public static Player getNearestPlayer(Location location) {
		Player result = null;
		double lastDistance = Double.MAX_VALUE;
		for(Player p : location.getWorld().getPlayers()) {
			if(p.getLocation().getBlockX() == p.getLocation().getBlockX() && p.getLocation().getBlockY() == p.getLocation().getBlockY() && p.getLocation().getBlockZ() == p.getLocation().getBlockZ())
				continue;

			double distance = location.distance(p.getLocation());
			if(distance < lastDistance) {
				lastDistance = distance;
				result = p;
			}
		}
		if(result != null)
			return result;
		return null;
	}

	public static Player getRandomPlayer(Collection<UUID> exclusions) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		UUID uuid;
		Random r = new Random();
		if(exclusions != null) {
			Stream<UUID> stream = players.stream().map(Player::getUniqueId).filter(u -> !exclusions.contains(u));
			long count = stream.count();
			long randomIndex = count <= Integer.MAX_VALUE ? r.nextInt((int) count) : r.longs(1, 0, count).findFirst().orElseThrow(AssertionError::new);
			uuid = stream.skip(randomIndex).findFirst().get();
		} else {
			Stream<UUID> stream = players.stream().map(Player::getUniqueId);
			long count = stream.count();
			long randomIndex = count <= Integer.MAX_VALUE ? r.nextInt((int) count) : r.longs(1, 0, count).findFirst().orElseThrow(AssertionError::new);
			uuid = stream.skip(randomIndex).findFirst().get();
		}
		return Bukkit.getPlayer(uuid);
	}

	public static Collection<Player> getAllPlayersInWorld(World world) {
		return world.getPlayers();
	}

	public static Collection<Entity> getAllEntitiesInWorld(World world) {
		return world.getEntities();
	}
}
