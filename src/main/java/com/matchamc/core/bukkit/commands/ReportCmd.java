package com.matchamc.core.bukkit.commands;

import java.util.Arrays;
import java.util.Collection;
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
		if(sender instanceof Player) {
			Player player = (Player) sender;
			Collection<Report> playerReports = reports.getReportsByUUID(player.getUniqueId());
			if(reports.getCooldown().contains(player.getUniqueId())) {
				player.sendMessage(MsgUtils.color("&cYou are unable to make a new report as you are on cooldown for 30 seconds."));
				return true;
			}
			if(!playerReports.isEmpty()) {
				int count = playerReports.size();
				if(count > 45) {
					int c = reports.cleanUpReports(player.getUniqueId());
					if(c == 0 || (count - c) > 45) {
						player.sendMessage(MsgUtils.color("&cYou are unable to make a new report."));
						player.sendMessage(MsgUtils.color("&cPlease wait for your old reports to be resolved before reporting again."));
						return true;
					}
					player.sendMessage(MsgUtils.color("&eThe plugin has deleted &a" + c + " &eof your resolved/closed reports."));
					player.sendMessage(MsgUtils.color("&ePlease retry the command again."));
					return true;
				}
			}
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
			reports.openReportReasonGUI(player, againstUUID);
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
		String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
		Report report = reports.createReport(reports.consoleUUID, againstUUID, reason, (!(sender instanceof Player)));
		sender.sendMessage(MsgUtils.color("&eYou have created a report &a#" + report.getId() + " &eagainst &a" + registrar.getNameFromRegistrar(report.getAgainstUUID()) + " &efor &a" + report.getReason() + "&e."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return null;
		return Collections.emptyList();
	}

}
