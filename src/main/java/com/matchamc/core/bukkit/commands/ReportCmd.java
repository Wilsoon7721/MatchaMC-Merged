package com.matchamc.core.bukkit.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.shared.MsgUtils;

public class ReportCmd extends CoreCommand {
	private PlayerRegistrar registrar;

	public ReportCmd(BukkitMain instance, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		// /report <name> <reason>
		if(args.length == 0) {
			sender.sendMessage(BukkitMain.INSUFFICIENT_PARAMETERS_ERROR);
			sender.sendMessage(MsgUtils.color("&cUsage: /report <player> <name>"));
			return true;
		}
		if(args.length < 2) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(BukkitMain.NON_PLAYER_ERROR);
				sender.sendMessage(MsgUtils.color("&cYou need to provide a reason for this report."));
				return true;
			}
			Player player = (Player) sender;
			Player against = Bukkit.getPlayer(args[1]);
			UUID againstUUID;
			if(against != null)
				againstUUID = against.getUniqueId();
			else 
				againstUUID = registrar.resolveUUIDFromName(args[1]);
			if(againstUUID == null) {
				sender.sendMessage(MsgUtils.color("&cThis player has not joined the server before."));
				return true;
			}
			openReportGUI(player, againstUUID);
			return true;
		}
		Player against = Bukkit.getPlayer(args[1]);
		UUID againstUUID;
		if(against != null)
			againstUUID = against.getUniqueId();
		else
			againstUUID = registrar.resolveUUIDFromName(args[1]);
		if(againstUUID == null) {
			sender.sendMessage(MsgUtils.color("&cThis player has not joined the server before."));
			return true;
		}
		// TODO create report
	}

	private void openReportGUI(Player player, UUID against) {
		// TODO GUI
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return null;
		return Collections.emptyList();
	}

}
