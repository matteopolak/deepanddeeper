package com.deepanddeeper.deepanddeeper;

import com.deepanddeeper.deepanddeeper.events.BlockBreakListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;

public final class DeepAndDeeper extends JavaPlugin {
	private FileConfiguration config = this.getConfig();
	private Database database;

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

		this.config.addDefault("database-uri", "jdbc:postgresql://localhost/deepanddeeper");
		this.saveConfig();

		try {
			this.database = new Database(this.config.getString("database-uri"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		try (Statement st = this.database.getConnection().createStatement()) {
			st.execute("""
				CREATE TABLE IF NOT EXISTS "user" (
					"uuid" UUID PRIMARY KEY,
				);
				
				CREATE TABLE IF NOT EXISTS "profile" (
					"id" SERIAL PRIMARY KEY,
					"user" UUID NOT NULL REFERENCES ("user"."uuid"),
					"coins" INT DEFAULT 0
				);
			""");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
