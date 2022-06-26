package com.matchamc.core.bukkit.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.matchamc.shared.MsgUtils;

public class Report {
	private Reports reports;
	private File file;
	private int id;
	private UUID reporter, against;
	private String reason, statusMessage;
	private long created;
	private Status status;
	private boolean prioritised;

	public Report(Reports reports, UUID reporter, UUID against, String reason, boolean prioritised) {
		this.reports = reports;
		id = this.reports.getNextAvailableId();
		this.reporter = reporter;
		this.against = against;
		this.reason = reason;
		created = System.currentTimeMillis();
		file = new File(this.reports.getReportsDirectory(), id + ".yml");
		status = Status.OPEN;
		this.prioritised = prioritised;
		try {
			file.createNewFile();
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cThe plugin could not create the report file.");
			ex.printStackTrace();
			return;
		}
		flush();
	}

	public Report(Reports reports, int id) {
		this.reports = reports;
		file = new File(this.reports.getReportsDirectory(), id + ".yml");
		if(!file.exists()) {
			MsgUtils.sendBukkitConsoleMessage("&cThe plugin could not retrieve the requested report with id #" + id + ".");
			MsgUtils.sendBukkitConsoleMessage("&cReason: The file does not exist.");
			return;
		}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		this.id = yc.getInt("id");
		this.reporter = UUID.fromString(yc.getString("reporter"));
		this.against = UUID.fromString(yc.getString("against"));
		this.reason = yc.getString("reason");
		this.created = yc.getLong("created");
		this.status = Status.valueOf(yc.getString("status"));
		this.statusMessage = yc.getString("status-message");
		this.prioritised = yc.getBoolean("priority");
	}

	public int getId() {
		return id;
	}

	public UUID getReporterUUID() {
		return reporter;
	}

	public UUID getAgainstUUID() {
		return against;
	}

	public String getReason() {
		return reason;
	}

	public long getCreatedInMillis() {
		return created;
	}

	public Status getStatus() {
		return status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public boolean isPrioritised() {
		return prioritised;
	}

	public void prioritised(boolean b) {
		prioritised = b;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatusMessage(String message) {
		statusMessage = message;
	}

	public void flush() {
		if(!file.exists())
			try {
				file.createNewFile();
			} catch(IOException ex) {}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
		yc.set("id", id);
		yc.set("reporter", reporter.toString());
		yc.set("against", against.toString());
		yc.set("reason", reason);
		yc.set("created", created);
		yc.set("status", status.toString().toUpperCase());
		yc.set("status-message", statusMessage);
		try {
			yc.save(file);
		} catch(IOException ex) {
			MsgUtils.sendBukkitConsoleMessage("&cReport #" + id + ": Could not save data to file.");
			ex.printStackTrace();
			return;
		}
		YamlConfiguration.loadConfiguration(file);
	}

	public static enum Status {
		OPEN, CLOSED, RESOLVED;
	}
}
