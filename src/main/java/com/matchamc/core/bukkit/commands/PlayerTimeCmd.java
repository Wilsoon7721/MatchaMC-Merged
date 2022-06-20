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

public class PlayerTimeCmd extends CoreCommand {
	private String permissionSet, permissionReset;
	public PlayerTimeCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		permissionSet = permissionNode + ".set";
		permissionReset = permissionNode + ".reset";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
			p.resetPlayerTime();
			p.sendMessage(MsgUtils.color("&eYour player time has been reset."));
			return true;
		}
		if(!(sender.hasPermission(permissionSet))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionSet));
			return true;
		}
		switch(args[0].toLowerCase()) {
		case "day":
		case "daytime":
		case "morning":
		case "dawn":
			p.setPlayerTime(1000L, false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time.self").replace("%time%", "1000")));
			break;
		case "night":
		case "nighttime":
		case "dusk":
			p.setPlayerTime(13000L, false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time.self").replace("%time%", "13000")));
			break;
		case "midnight":
			p.setPlayerTime(18000L, false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time.self").replace("%time%", "18000")));
			break;
		default:
			Integer i;
			try {
				i = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(MsgUtils.color("&cAn invalid value has been given."));
				return true;
			}
			p.setPlayerTime(i.longValue(), false);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.time.self").replace("%time%", String.valueOf(i))));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Arrays.asList("day", "night", "midnight").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

}
