package com.matchamc.core.bukkit.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class TeleportCmd extends CoreCommand {
	private String permissionPlayer, permissionCoords;
	public TeleportCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		permissionPlayer = permissionNode + ".player";
		permissionCoords = permissionNode + ".coords";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			if((args.length >= 0 && args.length < 2) || args.length < 4) {
				sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
				sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <target>"));
				sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <x> <y> <z> [world]"));
				return true;
			}
			if(args.length == 2) {
				Player player = Bukkit.getPlayer(args[0]);
				Player target = Bukkit.getPlayer(args[1]);
				if(player == null || target == null) {
					sender.sendMessage(MsgUtils.color("&eEither &c" + args[0] + " &eor &c" + args[1] + " &eis not online."));
					return true;
				}
				player.teleport(target);
				sender.sendMessage(MsgUtils.color("&eYou have teleported &a" + player.getName() + " &eto &a" + target.getName() + "&e."));
				return true;
			}
			if(args.length == 4) {
				Player target = Bukkit.getPlayer(args[0]);
				Double x, y, z;
				try {
					x = Double.parseDouble(args[1]);
					y = Double.parseDouble(args[2]);
					z = Double.parseDouble(args[3]);
				} catch(NumberFormatException ex) {
					sender.sendMessage(MsgUtils.color("&cYou did not provide a valid x, y, and/or z coordinate."));
					return true;
				}
				if(target == null) {
					sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
					return true;
				}
				Location location = new Location(target.getWorld(), x, y, z);
				target.teleport(location);
				sender.sendMessage(MsgUtils.color("&eYou have teleported &a" + target.getName() + " &eto &a" + target.getWorld().getName() + "&e, &a" + x + "&e, &a" + y + "&e, &a" + z + "&e."));
				return true;
			}
			if(args.length > 4) {
				Player target = Bukkit.getPlayer(args[0]);
				World world = Bukkit.getWorld(args[4]);
				Double x, y, z;
				try {
					x = Double.parseDouble(args[1]);
					y = Double.parseDouble(args[2]);
					z = Double.parseDouble(args[3]);
				} catch(NumberFormatException ex) {
					sender.sendMessage(MsgUtils.color("&cYou did not provide a valid x, y, and/or z coordinate."));
					return true;
				}
				if(target == null) {
					sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
					return true;
				}
				if(world == null) {
					sender.sendMessage(MsgUtils.color("&cThis world does not exist."));
					return true;
				}
				Location location = new Location(world, x, y, z);
				target.teleport(location);
				sender.sendMessage(MsgUtils.color("&eYou have teleported &a" + target.getName() + " &eto &a" + world.getName() + "&e, &a" + x + "&e, &a" + y + "&e, &a" + z + "&e."));
				return true;
			}
			return true;
		}
		// TODO Player TP Commands
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <x> <y> <z>"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <x> <y> <z> [world]"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player>"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <target>"));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

	}

}
