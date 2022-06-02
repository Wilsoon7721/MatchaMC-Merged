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

public class FeedCmd extends CoreCommand {

	public FeedCmd(BukkitMain instance, String permissionNode) {
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
			((Player) sender).setFoodLevel(20);
			sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.feed.main_message")));
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
		target.setFoodLevel(20);
		target.sendMessage(MsgUtils.color(instance.messages().getString("commands.feed.main_message")));
		sender.sendMessage(MsgUtils.color(instance.messages().getString("commands.feed.fed_others")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return Collections.emptyList();
	}

}
