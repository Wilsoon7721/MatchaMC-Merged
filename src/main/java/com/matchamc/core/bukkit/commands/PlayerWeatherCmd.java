package com.matchamc.core.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class PlayerWeatherCmd extends CoreCommand {
	private String permissionSet, permissionClear, permissionRain, permissionReset;
	public PlayerWeatherCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		permissionSet = permissionNode + ".set";
		permissionReset = permissionNode + ".reset";
		permissionClear = permissionSet + ".sun";
		permissionRain = permissionSet + ".rain";
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
		if(args.length == 0) {
			if(!(sender.hasPermission(permissionReset))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionReset));
				return true;
			}
			p.resetPlayerWeather();
			p.sendMessage(MsgUtils.color("&eYour player weather has been reset."));
			return true;
		}
		if(!(sender.hasPermission(permissionSet))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionSet));
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "day":
		case "clear":
		case "sun":
			if(!(sender.hasPermission(permissionClear))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionClear));
				return true;
			}
			p.setPlayerWeather(WeatherType.CLEAR);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather.self").replace("%weather%", "sun")));
			break;
		case "rain":
		case "precipitation":
			if(!(sender.hasPermission(permissionRain))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionRain));
				return true;
			}
			p.setPlayerWeather(WeatherType.DOWNFALL);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather.self").replace("%weather%", "rain")));
			break;
		case "thunder":
		case "storm":
			if(!(sender.hasPermission(permissionRain))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionRain));
				return true;
			}
			p.setPlayerWeather(WeatherType.DOWNFALL);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather.self").replace("%weather%", "rain")));
			sender.sendMessage(MsgUtils.color("&cThe player weather cannot be changed to thunderstorm."));
			break;
		default:
			if(!(sender.hasPermission(permissionClear))) {
				sender.sendMessage(instance.formatNoPermsMsg(permissionClear));
				return true;
			}
			p.getWorld().setThundering(false);
			p.getWorld().setStorm(false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.weather.self").replace("%weather%", "sun")));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return StringUtil.copyPartialMatches(args[0], Arrays.asList("sun", "rain"), new ArrayList<>());
		return Collections.emptyList();
	}

}
