package com.matchamc.automod.bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import com.matchamc.shared.MsgUtils;

public class Violations {
	private AutoMod autoMod;
	private HashMap<UUID, Set<Violation>> playerViolations = new HashMap<>();
	private Connection con; // Database connection to store violations

	public Violations(AutoMod autoMod) {
		this.autoMod = autoMod;
	}

	public AutoMod getInstance() {
		return autoMod;
	}

	public Set<Violation> getViolations(UUID uuid, Module module) {
		Set<Violation> violations = playerViolations.get(uuid);
		if(violations.isEmpty())
			return null;
		return violations;
	}

	public void setupDatabase(String host, int port, String databaseName, String username, String password) {
		String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch(SQLException ex) {
			sql(ex);
			return;
		}
	}

	public Connection getDatabase() {
		try {
			if(con.isClosed() || con == null)
				return null;
			return con;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void sql(SQLException ex) {
		MsgUtils.sendBukkitConsoleMessage("&c[Violations - Database] Encountered an SQL Exception!");
		MsgUtils.sendBukkitConsoleMessage("&c  |- Error Message: " + ex.getMessage());
		MsgUtils.sendBukkitConsoleMessage("&c  |- Error Code: " + ex.getErrorCode());
		MsgUtils.sendBukkitConsoleMessage("&c  |- SQL State: " + ex.getSQLState());
	}
}
