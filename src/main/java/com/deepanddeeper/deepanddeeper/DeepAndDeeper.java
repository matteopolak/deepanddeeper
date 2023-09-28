package com.deepanddeeper.deepanddeeper;

import com.deepanddeeper.deepanddeeper.events.PlayerJoinListener;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeepAndDeeper extends JavaPlugin {
	private FileConfiguration config = this.getConfig();
	private Database database;

	public World lobby = this.getServer().getWorld("world");

	@Override
	public void onEnable() {
		Listener[] listeners = {
			new PlayerJoinListener(this.lobby),
		};

		PluginManager manager = this.getServer().getPluginManager();

		for (Listener listener : listeners) {
			manager.registerEvents(listener, this);
		}

		this.config.addDefault("database-uri", "jdbc:postgresql://localhost/deepanddeeper");
		this.saveConfig();

		/*try {
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
		}*/
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
