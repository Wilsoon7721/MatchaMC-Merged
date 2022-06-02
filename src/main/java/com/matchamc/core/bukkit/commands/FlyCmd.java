package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.MsgUtils;

public class FlyCmd extends CoreCommand {

	public FlyCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(permissionNode)) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				return true;
			}
			Player player = ((Player) sender);
			player.setAllowFlight(!player.getAllowFlight());
			if(player.getAllowFlight()) {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.main_message.enabled")));
			} else {
				sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.main_message.disabled")));
			}
			return true;
		}
		if(!sender.hasPermission(permissionNode + ".others")) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode + ".others"));
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		target.setAllowFlight(!target.getAllowFlight());
		if(target.getAllowFlight()) {
			target.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.main_message.enabled")));
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.fly_others").replace("%state%", "&aenabled").replace("%player%", target.getName())));
		} else {
			target.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.main_message.disabled")));
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.fly.fly_others").replace("%state%", "&cdisabled").replace("%player%", target.getName())));
		}
		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

}
