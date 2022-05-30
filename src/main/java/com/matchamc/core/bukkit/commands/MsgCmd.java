package com.matchamc.core.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;

public class MsgCmd extends CoreCommand {

	public MsgCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	// Include a /r command too
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) { 
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}

	}

}
