package com.matchamc.core.bukkit.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;

public class TeleportCmd extends CoreCommand {

	public TeleportCmd(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

	}

}
