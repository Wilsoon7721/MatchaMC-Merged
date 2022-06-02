package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class SpeedCmd extends CoreCommand {
	private String permissionWalk, permissionFly, permissionOthers;
	public SpeedCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		this.permissionWalk = permissionNode + ".walk";
		this.permissionFly = permissionNode + ".fly";
		this.permissionOthers = permissionNode + ".others";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&eYour walk speed is &a" + player.getWalkSpeed() * 10));
			sender.sendMessage(MsgUtils.color("&eYour fly speed is &a" + player.getFlySpeed() * 10));
			return true;
		}
		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
			case "walk":
				sender.sendMessage(MsgUtils.color("&eYour walk speed is &a" + player.getWalkSpeed() * 10));
				break;
			case "fly":
				sender.sendMessage(MsgUtils.color("&eYour fly speed is &a" + player.getFlySpeed() * 10));
				break;
			}
			if(!sender.hasPermission(permissionWalk))
				sender.sendMessage(MsgUtils.color("&cYou do not have permission to change your walk speed."));
			if(!sender.hasPermission(permissionFly))
				sender.sendMessage(MsgUtils.color("&cYou do not have permission to change your fly speed."));
			return true;
		}
		if(args.length == 2) {
			Double speed;
			try {
				speed = Double.parseDouble(args[1]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.invalid_speed")));
				return true;
			}
			float formattedSpeed = (speed.floatValue() / 10);
			if(args[0].equalsIgnoreCase("walk")) {
				if(!sender.hasPermission(permissionWalk)) {
					sender.sendMessage(MsgUtils.color("&cYou do not have permission to change your walk speed."));
					return true;
				}
				player.setWalkSpeed(formattedSpeed);
				player.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed").replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				return true;
			}
			if(args[0].equalsIgnoreCase("fly")) {
				if(!sender.hasPermission(permissionFly)) {
					sender.sendMessage(MsgUtils.color("&cYou do not hvae permission to change your fly speed."));
					return true;
				}
				player.setFlySpeed(formattedSpeed);
				player.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed").replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				return true;
			}
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.invalid_section")));
			return true;
		}
		if(args.length > 2) {
			Double speed;
			try {
				speed = Double.parseDouble(args[1]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.invalid_speed")));
				return true;
			}
			if(!sender.hasPermission(permissionOthers)) {
				sender.sendMessage(MsgUtils.color("&cYou do not have permission to change others' speed."));
				return true;
			}
			float formattedSpeed = (speed.floatValue() / 10);
			Player target = Bukkit.getPlayer(args[2]);
			if(target == null) {
				sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
				return true;
			}
			if(args[0].equalsIgnoreCase("walk")) {
				target.setWalkSpeed(formattedSpeed);
				target.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed").replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed_others").replace("%player%", target.getName()).replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				return true;
			}
			if(args[0].equalsIgnoreCase("fly")) {
				target.setFlySpeed(formattedSpeed);
				target.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed").replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.update_speed_others").replace("%player%", target.getName()).replace("%type%", args[0].toLowerCase()).replace("%speed%", speed.toString())));
				return true;
			}
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.speed.invalid_section")));
			return true;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Arrays.asList("walk", "fly");
		return Collections.emptyList();
	}
}
