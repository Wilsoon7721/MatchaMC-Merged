package com.matchamc.core.bukkit.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class WeatherCmd extends CoreCommand {
	private String permissionClear, permissionRain, permissionStorm;
	public WeatherCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		permissionClear = permissionNode + ".sun";
		permissionRain = permissionNode + ".rain";
		permissionStorm = permissionNode + ".storm";
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
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /weather <clear/rain/thunder>"));
			return true;
		}
		Player p = (Player) sender;
		switch(args[0].toLowerCase()) {
		case "day":
		case "clear":
		case "sun":
			if(!(sender.hasPermission(permissionClear))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionClear));
				return true;
			}
			p.getWorld().setThundering(false);
			p.getWorld().setStorm(false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather").replace("%weather%", "sun")));
			break;
		case "rain":
		case "precipitation":
			if(!(sender.hasPermission(permissionRain))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionRain));
				return true;
			}
			p.getWorld().setStorm(true);
			p.getWorld().setThundering(false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather").replace("%weather%", "rain")));
			break;
		case "thunder":
		case "storm":
		case "lightning":
			if(!(sender.hasPermission(permissionStorm))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionStorm));
				return true;
			}
			p.getWorld().setStorm(true);
			p.getWorld().setThundering(true);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather").replace("%weather%", "storm")));
			break;
		default:
			if(!(sender.hasPermission(permissionClear))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionClear));
				return true;
			}
			p.getWorld().setThundering(false);
			p.getWorld().setStorm(false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather").replace("%weather%", "sun")));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

	}

}
