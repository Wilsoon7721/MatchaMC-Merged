package com.matchamc.core.bungee.commands;

import com.matchamc.core.bungee.BungeeMain;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SendToAllCmd extends Command {
	// Issue a command to all servers
	public SendToAllCmd() {
		super("sendtoall");

	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender.hasPermission("core.bungee.sendtoall"))) {
			sender.sendMessage(BungeeMain.NO_PERMISSION_ERROR);
			return;
		}
		String command = String.join(" ", args).trim();
	}

}
