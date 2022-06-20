package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.SelectorUtil;
import com.matchamc.shared.MsgUtils;

public class TeleportHereCmd extends CoreCommand {
	// This command supports basic selectors (@a/@e/@p/@r)
	public TeleportHereCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		Player p = (Player) sender;
		Player target;
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /teleporthere (Player)"));
			return true;
		}
		if(args[0].equalsIgnoreCase("@p")) {
			if(!(sender.hasPermission(SelectorUtil.SELECTOR_USAGE_PERMISSION))) {
				sender.sendMessage(SelectorUtil.SELECTOR_USAGE_PERMISSION_MESSAGE);
				return true;
			}
			target = SelectorUtil.getNearestPlayer(p.getLocation(), Arrays.asList(p.getUniqueId()));
			if(target == null) {
				sender.sendMessage(MsgUtils.color("&cFailed to use selector: Nearest player was not found."));
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("@r")) {
			if(!(sender.hasPermission(SelectorUtil.SELECTOR_USAGE_PERMISSION))) {
				sender.sendMessage(SelectorUtil.SELECTOR_USAGE_PERMISSION_MESSAGE);
				return true;
			}
			target = SelectorUtil.getRandomPlayer(Arrays.asList(p.getUniqueId()));
			if(target == null) {
				sender.sendMessage(MsgUtils.color("&cFailed to use selector: Random player was not found."));
				return true;
			}
		}
		if(args[0].equalsIgnoreCase("@a")) {
			if(!(sender.hasPermission(SelectorUtil.SELECTOR_USAGE_PERMISSION))) {
				sender.sendMessage(SelectorUtil.SELECTOR_USAGE_PERMISSION_MESSAGE);
				return true;
			}
			Collection<Player> players = SelectorUtil.getAllPlayersInWorld(p.getWorld());
			for(Player pl : players) {
				pl.teleport(p);
			}
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleporthere").replace("%player%", "" + players.size() + " players")));
			return true;
		}
		if(args[0].equalsIgnoreCase("@e")) {
			if(!(sender.hasPermission(SelectorUtil.SELECTOR_USAGE_PERMISSION))) {
				sender.sendMessage(SelectorUtil.SELECTOR_USAGE_PERMISSION_MESSAGE);
				return true;
			}
			Collection<Entity> entities = SelectorUtil.getAllEntitiesInWorld(p.getWorld());
			for(Entity entity : entities) {
				entity.teleport(p);
			}
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleporthere").replace("%player%", "" + entities.size() + " entities")));
			return true;
		}
		target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		target.teleport(p);
		sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleporthere").replace("%player%", target.getName())));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}
}
