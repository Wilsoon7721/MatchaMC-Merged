package com.matchamc.core.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.Messenger;

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

	}

}
