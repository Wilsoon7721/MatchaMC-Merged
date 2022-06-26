package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Report;
import com.matchamc.core.bukkit.util.Reports;
import com.matchamc.shared.MsgUtils;

public class ReportCmd extends CoreCommand {
	private Reports reports;
	private PlayerRegistrar registrar;

	public ReportCmd(BukkitMain instance, Reports reports, PlayerRegistrar registrar, String permissionNode) {
		super(instance, permissionNode);
		this.reports = reports;
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
		String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
		Report report = reports.createReport(reports.consoleUUID, againstUUID, reason, (!(sender instanceof Player)));
		sender.sendMessage(MsgUtils.color("&eYou have created a report &a#" + report.getId() + " &eagainst &a" + registrar.getNameFromRegistrar(report.getAgainstUUID()) + " &efor &a" + report.getReason() + "&e."));
		return true;
	}

	private void openReportGUI(Player player, UUID against) {
		Inventory inv = Bukkit.createInventory(null);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return null;
		return Collections.emptyList();
	}

}
