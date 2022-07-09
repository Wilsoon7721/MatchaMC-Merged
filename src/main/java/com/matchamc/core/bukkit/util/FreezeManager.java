package com.matchamc.core.bukkit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.StringUtil;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class FreezeManager extends CoreCommand implements Listener {
	private PlayerRegistrar registrar;
	private Set<UUID> frozen = new HashSet<>();

	public FreezeManager(BukkitMain instance, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /freeze <player>"));
			return true;
		}
		UUID u = registrar.resolveUUIDFromName(args[0]);
		if(u == null) {
			sender.sendMessage(MsgUtils.color("&cNo player matching this name was found."));
			return true;
		}
		if(isFrozen(u)) {
			remove(u);
			sender.sendMessage(MsgUtils.color("&eYou have unfrozen &a" + args[0] + "&e."));
			return true;
		}
		add(u);
		sender.sendMessage(MsgUtils.color("&eYou have frozen &a" + args[0] + "&e."));
		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!isFrozen(event.getPlayer().getUniqueId()))
			return;
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage(MsgUtils.color("&cYou are unable to move while frozen."));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!isFrozen(event.getPlayer().getUniqueId()))
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage(MsgUtils.color("&cYou are unable to break blocks while frozen."));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!isFrozen(event.getPlayer().getUniqueId()))
			return;
		event.setCancelled(true);
		event.getPlayer().sendMessage(MsgUtils.color("&cYou are unable to place blocks while frozen."));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFrozenHitEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if(!isFrozen(damager.getUniqueId()))
				return;
			event.setCancelled(true);
			damager.sendMessage(MsgUtils.color("&cYou cannot hit this player while frozen."));
			return;
		}
		if(event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			if(!isFrozen(damaged.getUniqueId()))
				return;
			event.setCancelled(true);
			event.getDamager().sendMessage(MsgUtils.color("&cYou cannot hit this player."));
			return;
		}
	}

	@EventHandler
	public void onFrozenDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if(!isFrozen(player.getUniqueId()))
			return;
		event.setCancelled(true);
	}

	public void add(UUID uuid) {
		frozen.add(uuid);
	}

	public void remove(UUID uuid) {
		frozen.remove(uuid);
	}

	public boolean isFrozen(UUID uuid) {
		return frozen.contains(uuid);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return StringUtil.copyPartialMatches(args[0], registrar.getAllRegisteredPlayerNames(), new ArrayList<>());
		return Collections.emptyList();
	}

}
