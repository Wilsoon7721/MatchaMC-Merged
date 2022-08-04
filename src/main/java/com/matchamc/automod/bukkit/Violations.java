package com.matchamc.automod.bukkit;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			Connection con = DriverManager.getConnection(url, username, password);
			if(!con.isValid(30))
				throw new SQLException("Connection invalidated after 30 seconds", "INVALIDATED", -1);
			this.con = con;
			DatabaseMetaData dbm = this.con.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "violations", null);
			if(!tables.next()) {
				PreparedStatement stmt = con.prepareStatement("CREATE TABLE violations(id int AUTO_INCREMENT, violator_uuid varchar(255), module varchar(255))");
				stmt.executeUpdate();
				MsgUtils.sendBukkitConsoleMessage("&e[Violations - Database] Executed database update to create violations table.");
				stmt.close();
			}
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
