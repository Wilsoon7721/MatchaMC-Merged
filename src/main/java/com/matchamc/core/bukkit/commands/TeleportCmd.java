package com.matchamc.core.bukkit.commands;

import java.util.Collections;
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
	private String permissionPlayer, permissionCoords, permissionPlayerOthers, permissionCoordsOthers, permissionAcrossWorlds, customOfflineMessageKey = "commands.teleport.player.player_offline";
	public TeleportCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		permissionPlayer = permissionNode + ".player";
		permissionPlayerOthers = permissionPlayer + ".others"; // Tp someone else to another player
		permissionCoords = permissionNode + ".coords";
		permissionCoordsOthers = permissionCoords + ".others"; // Tp someone else to a coordinate
		permissionAcrossWorlds = permissionNode + ".acrossworlds";
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
				if(!(sender.hasPermission(permissionPlayerOthers))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionPlayerOthers));
					return true;
				}
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
				if(!(sender.hasPermission(permissionCoordsOthers))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionCoordsOthers));
					return true;
				}
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
					if(instance.messages().getString(customOfflineMessageKey) != null)
						sender.sendMessage(MsgUtils.color(instance.messages().getString(customOfflineMessageKey)));
					else 
						sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
					return true;
				}
				Location location = new Location(target.getWorld(), x, y, z);
				target.teleport(location);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success_others").replace("%player%", target.getName()).replace("%world%", target.getWorld().getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
				return true;
			}
			if(args.length > 4) {
				if(!(sender.hasPermission(permissionCoordsOthers))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionCoordsOthers));
					return true;
				} else if(!(sender.hasPermission(permissionAcrossWorlds))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionAcrossWorlds));
					return true;
				}
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
					if(instance.messages().getString(customOfflineMessageKey) != null)
						sender.sendMessage(MsgUtils.color(instance.messages().getString(customOfflineMessageKey)));
					else
						sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
					return true;
				}
				if(world == null) {
					sender.sendMessage(MsgUtils.color("&cThis world does not exist."));
					return true;
				}
				Location location = new Location(world, x, y, z);
				target.teleport(location);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success_others").replace("%player%", target.getName()).replace("%world%", world.getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
				return true;
			}
			return true;
		}
		Player p = (Player) sender;
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <x> <y> <z> [world]"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <x> <y> <z> [world]"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player>"));
			sender.sendMessage(MsgUtils.color("&cUsage: /teleport <player> <target>"));
			return true;
		}
		if(args.length == 1) {
			if(!(sender.hasPermission(permissionPlayer))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionPlayer));
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				if(instance.messages().getString(customOfflineMessageKey) != null)
					sender.sendMessage(MsgUtils.color(instance.messages().getString(customOfflineMessageKey)));
				else
					sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
				return true;
			}
			p.teleport(target);
			p.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.player.success").replace("%player%", target.getName())));
			return true;
		}
		if(args.length == 2) {
			if(!(sender.hasPermission(permissionPlayerOthers))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionPlayerOthers));
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			Player tpTo = Bukkit.getPlayer(args[1]);
			if(target == null || tpTo == null) {
				if(instance.messages().getString(customOfflineMessageKey) != null)
					sender.sendMessage(MsgUtils.color(instance.messages().getString(customOfflineMessageKey)));
				else
					sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
				return true;
			}
			target.teleport(tpTo);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.player.success_others").replace("%player%", target.getName()).replace("%target%", tpTo.getName())));
			return true;
		}
		if(args.length == 3) { // /tp <x> <y> <z>
			if(!(sender.hasPermission(permissionCoords))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionCoords));
				return true;
			}
			Double x, y, z;
			try {
				x = Double.parseDouble(args[0]);
				y = Double.parseDouble(args[1]);
				z = Double.parseDouble(args[2]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(MsgUtils.color("&cYou did not provide a valid x, y, and/or z coordinate."));
				return true;
			}
			Location location = new Location(p.getWorld(), x, y, z);
			p.teleport(location);
			p.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success").replace("%world%", p.getWorld().getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
			return true;
		}
		if(args.length == 4) { // /tp <x> <y> <z> (world) OR /tp <player> <x> <y> <z>
			boolean parseByPlayer = false;
			try {
				Double.parseDouble(args[0]);
			} catch(NumberFormatException ex) {
				parseByPlayer = true;
			}
			if(parseByPlayer) {
				if(!(sender.hasPermission(permissionCoordsOthers))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionCoordsOthers));
					return true;
				}
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
				Location loc = new Location(target.getWorld(), x, y, z);
				target.teleport(loc);
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success_others").replace("%player%", target.getName()).replace("%world%", target.getWorld().getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
			} else {
				if(!(sender.hasPermission(permissionCoords))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionCoords));
					return true;
				} else if(!(sender.hasPermission(permissionAcrossWorlds))) {
					sender.sendMessage(instance.formatNoPermsMsg(permissionAcrossWorlds));
					return true;
				}
				Double x, y, z;
				World world = Bukkit.getWorld(args[3]);
				try {
					x = Double.parseDouble(args[0]);
					y = Double.parseDouble(args[1]);
					z = Double.parseDouble(args[2]);
				} catch(NumberFormatException ex) {
					sender.sendMessage(MsgUtils.color("&cYou did not provide a valid x, y, and/or z coordinate."));
					return true;
				}
				if(world == null) {
					sender.sendMessage(MsgUtils.color("&cThis world does not exist."));
					return true;
				}
				Location loc = new Location(world, x, y, z);
				p.teleport(loc);
				p.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success").replace("%world%", world.getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
			}
			return true;
		}
		if(args.length > 4) { // /tp <player> <x> <y> <z> (world)
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
				if(instance.messages().getString(customOfflineMessageKey) != null)
					sender.sendMessage(MsgUtils.color(instance.messages().getString(customOfflineMessageKey)));
				else
					sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
				return true;
			}
			if(world == null) {
				sender.sendMessage(MsgUtils.color("&cThis world does not exist."));
				return true;
			}
			Location loc = new Location(world, x, y, z);
			target.teleport(loc);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.teleport.coordinates.success_others").replace("%player%", target.getName()).replace("%world%", world.getName()).replace("%x-coord%", x.toString()).replace("%y-coord%", y.toString()).replace("%z-coord%", z.toString())));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
