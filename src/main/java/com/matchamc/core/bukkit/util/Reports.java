package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Reports {
	private BukkitMain instance;
	private PlayerRegistrar registrar;
	private File reportsDirectory;
	public UUID consoleUUID = UUID.fromString("5aa66c90-aee9-4bb1-987b-1b307d77e4ca");
	public String notifyReportMadePermission = "staffcore.notify.reports.created";
	public String notifyReportClosedPermission = "staffcore.notify.reports.closed";

	public Reports(BukkitMain instance, PlayerRegistrar registrar) {
		this.instance = instance;
		this.registrar = registrar;
		reportsDirectory = new File(this.instance.getDataFolder(), "/reports/");
		if(!reportsDirectory.exists())
			reportsDirectory.mkdirs();
	}

	public Report createReport(UUID reporter, UUID against, String reason, boolean priority) {
		Report report = new Report(this, reporter, against, reason, priority);
		notifyReportMade(report);
		return report;
	}

	public Report getReport(int id) {
		return new Report(this, id);
	}

	public int getNextAvailableId() {
		if(reportsDirectory.listFiles().length == 0)
			return 1;
		try {
			List<Integer> reportIds = Stream.of(reportsDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".yml"))
						return true;
					return false;
				}
			})).map(File::getName).map(s -> s.replaceAll("\\D*", "")).map(Integer::parseInt).collect(Collectors.toList());
			int highest = Collections.max(reportIds);
			return (highest + 1);
		} catch(NumberFormatException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cFailed to get next available id from Reports.");
			MsgUtils.sendBukkitConsoleMessage("&cConfiguration error - A Report ID is unable to parse through Integer#parseInt.");
			return -1;
		}
	}

	public void notifyReportMade(Report report) {
		String reporterName = registrar.getNameFromRegistrar(report.getReporterUUID()), againstName = registrar.getNameFromRegistrar(report.getAgainstUUID());
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.hasPermission(notifyReportMadePermission)) continue;
			if(report.isPrioritised()) player.sendMessage(MsgUtils.color("&c&l[REPORT CREATED] &c(ID #" + report.getId() + ") &6" + reporterName + " &chas reported &6" + againstName + " &cfor &6" + report.getReason() + "&c."));
			else player.sendMessage(MsgUtils.color("&3[&bREPORT CREATED&3] &9(ID #" + report.getId() + ") &b" + reporterName + " &3has reported &b" + againstName + " &3for &b" + report.getReason() + "&3."));
		}
	}

	public File getReportsDirectory() {
		return reportsDirectory;
	}
}
