package com.matchamc.core.bukkit.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Messenger;
import com.matchamc.shared.util.MsgUtils;

public class MsgCmd extends CoreCommand {
	private Messenger messenger;

	public MsgCmd(BukkitMain instance, Messenger messenger, String permissionNode) {
		super(instance, permissionNode);
		this.messenger = messenger;
	}

	// Include a /r command too
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) { 
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length >= 0 && args.length < 2) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			return true;
		}
		String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		Player target = Bukkit.getPlayer(args[0]);
		if(target == null) {
			sender.sendMessage(BukkitMain.PLAYER_OFFLINE);
			return true;
		}
		messenger.sendMessage(sender, target, MsgUtils.color(message));
		return true;
	}
}
