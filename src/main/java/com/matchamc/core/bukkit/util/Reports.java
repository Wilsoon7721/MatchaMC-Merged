package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.matchamc.core.bukkit.BukkitMain;
import com.matchamc.shared.MsgUtils;

public class Reports {
	private BukkitMain instance;
	private File reportsDirectory;
	public Reports(BukkitMain instance) {
		this.instance = instance;
		reportsDirectory = new File(this.instance.getDataFolder(), "/reports/");
		if(!reportsDirectory.exists())
			reportsDirectory.mkdirs();
	}

	public Report createReport(UUID reporter, UUID against, String reason) {
		return new Report(this, reporter, against, reason);
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

	public File getReportsDirectory() {
		return reportsDirectory;
	}
}
