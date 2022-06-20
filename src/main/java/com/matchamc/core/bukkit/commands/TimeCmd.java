package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class TimeCmd extends CoreCommand {

	public TimeCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			return true;
		}
		Player p = (Player) sender;
		switch(args[0].toLowerCase()) {
		case "day":
		case "daytime":
		case "morning":
		case "dawn":
			p.getWorld().setTime(1000L);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time").replace("%time%", "1000")));
			break;
		case "night":
		case "nighttime":
		case "dusk":
			p.getWorld().setTime(13000L);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time").replace("%time%", "13000")));
			break;
		case "midnight":
			p.getWorld().setTime(18000L);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time").replace("%time%", "18000")));
			break;
		default:
			Integer i;
			try {
				i = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(MsgUtils.color("&cAn invalid value has been given."));
				return true;
			}
			p.getWorld().setTime(i.longValue());
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time").replace("%time%", String.valueOf(i))));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Arrays.asList("day", "night").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

}
