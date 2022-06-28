package com.matchamc.core.bukkit.commands;

import java.util.Collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.core.bukkit.util.CoreCommand;
import com.matchamc.core.bukkit.util.PlayerRegistrar;
import com.matchamc.core.bukkit.util.Report;
import com.matchamc.core.bukkit.util.Reports;
import com.matchamc.core.bukkit.util.Staffs;
import com.matchamc.shared.MsgUtils;

public class ReportsCmd extends CoreCommand {
	private PlayerRegistrar registrar;
	private Staffs staffs;
	private Reports reports;

	public ReportsCmd(BukkitMain instance, PlayerRegistrar registrar, Staffs staffs, Reports reports, String permissionNode) {
		super(instance, permissionNode);
		this.registrar = registrar;
		this.staffs = staffs;
		this.reports = reports;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender.hasPermission(permissionNode))) {
			sender.sendMessage(instance.formatNoPermsMsg(permissionNode));
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(MsgUtils.color("&eYour Reports"));
			Collection<Report> consoleReports = reports.getReportsByUUID(reports.consoleUUID);
			consoleReports.stream().forEachOrdered(report -> sender.sendMessage(MsgUtils.color("&eReport ID &a#" + report.getId() + " &e| You reported &a" + registrar.getNameFromRegistrar(report.getAgainstUUID()) + " &efor &c" + report.getReason() + "&e.")));
			return true;
		}

	}
}
