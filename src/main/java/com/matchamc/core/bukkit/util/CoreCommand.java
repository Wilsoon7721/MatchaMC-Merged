package com.matchamc.core.bukkit.util;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.matchamc.core.bukkit.BukkitMain;

public abstract class CoreCommand implements CommandExecutor, TabExecutor {
	protected BukkitMain instance;
	protected String permissionNode;

	public CoreCommand(BukkitMain instance, String permissionNode) {
		this.instance = instance;
		this.permissionNode = permissionNode;
	}

	@Override
	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

	@Override
	public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args);

	public String permissionNode() {
		return permissionNode;
	}
}
