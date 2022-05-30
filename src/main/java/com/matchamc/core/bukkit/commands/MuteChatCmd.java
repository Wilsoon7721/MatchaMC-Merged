package com.matchamc.core.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.shared.util.MsgUtils;

public class MuteChatCmd extends CoreCommand {

	public MuteChatCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(MsgUtils.color(BukkitMain.NO_PERMISSION_ERROR.replace("%permission%", permissionNode)));
			return true;
		}
		// TODO MuteChat
		return true;
	}

}
