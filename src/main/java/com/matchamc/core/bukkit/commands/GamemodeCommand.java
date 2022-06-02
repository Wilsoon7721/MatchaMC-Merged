package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
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

public class GamemodeCommand extends CoreCommand {
	private String permissionCreative, permissionSurvival, permissionAdventure, permissionSpectator, permissionOthers;
	private List<String> gamemodes = Arrays.asList("creative", "survival", "adventure", "spectator");
	public GamemodeCommand(BukkitMain instance, String permissionNode) {
		super(instance, permissionNode);
		this.permissionCreative = permissionNode + ".creative";
		this.permissionSurvival = permissionNode + ".survival";
		this.permissionAdventure = permissionNode + ".adventure";
		this.permissionSpectator = permissionNode + ".spectator";
		this.permissionOthers = permissionNode + ".others";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(MsgUtils.color("&cUsage: /gamemode <mode> <player>"));
			return true;
		}
		if(args.length == 1) {
			switch(args[0].toLowerCase()) {
			case "c":
			case "creative":

				break;
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) 
			return gamemodes.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		if(args.length == 1)
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
		return Collections.emptyList();
	}
}
