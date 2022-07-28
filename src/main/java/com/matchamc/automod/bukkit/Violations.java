package com.matchamc.automod.bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class Violations {
	private AutoMod autoMod;
	private Connection con; // Database connection to store violations

	public Violations(AutoMod autoMod) {
		this.autoMod = autoMod;
	}

	public Set<Violation> getViolations(UUID uuid, Module module) {

	}

	public void setupDatabase(String host, int port,)

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
}
