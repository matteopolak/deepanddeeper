package com.deepanddeeper.deepanddeeper;


import com.deepanddeeper.deepanddeeper.commands.GetWorldCommand;
import com.deepanddeeper.deepanddeeper.events.PartyEventListener;
import com.deepanddeeper.deepanddeeper.events.PlayerJoinListener;
import com.deepanddeeper.deepanddeeper.game.GameManager;
import com.deepanddeeper.deepanddeeper.party.PartyManager;

import com.deepanddeeper.deepanddeeper.commands.StartGameCommand;
import com.deepanddeeper.deepanddeeper.events.EntityClickListener;

import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import com.deepanddeeper.deepanddeeper.weapons.WeaponHolder;
import org.bukkit.Bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class DeepAndDeeper extends JavaPlugin {
	private FileConfiguration config = this.getConfig();
	public Database database;

	public WeaponHolder weaponHolder;

	public PartyManager partyManager = new PartyManager();
	public GameManager gameManager = new GameManager(this);

	private void registerListeners() {
		// Add event listeners here

		Listener[] listeners = {
			new PartyEventListener(this),
			new PlayerJoinListener(this),
			new EntityClickListener(this),
		};

		PluginManager manager = this.getServer().getPluginManager();

		for (Listener listener : listeners) {
			manager.registerEvents(listener, this);
		}
	}

	private void registerCommands() {
		// Add commands here
		CommandWithName[] commands = {
			new GetWorldCommand(),
			new StartGameCommand(this),
		};

		for (CommandWithName command : commands) {
			this.getCommand(command.commandName()).setExecutor(command);
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, "Hello, world!");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		this.saveDefaultConfig();

		this.weaponHolder = new WeaponHolder(((List<Object>) this.getConfig().getList("weapons")).stream()
				.map(weapon -> Weapon.deserialize((Map<String, Object>) weapon))
				.toList());

		this.registerCommands();
		this.registerListeners();


		this.config.addDefault("database-uri", "jdbc:postgresql://localhost/deepanddeeper");
		this.saveConfig();

		try {
			this.database = new Database(this.config.getString("database-uri"));
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		try (Statement st = this.database.getConnection().createStatement()) {
			st.execute("""
				CREATE TABLE IF NOT EXISTS "user" (
					"uuid" UUID PRIMARY KEY
				);

				CREATE TABLE IF NOT EXISTS "profile" (
					"id" SERIAL PRIMARY KEY,
					"user" UUID NOT NULL,
					"active" BOOLEAN NOT NULL,
					"coins" INT DEFAULT 0,
					"wins" INT DEFAULT 0,
					"losses" INT DEFAULT 0,
					"kills" INT DEFAULT 0,
					"deaths" INT DEFAULT 0,
					"xp" INT DEFAULT 0
				);

				CREATE TABLE IF NOT EXISTS "stash" (
					"profile_id" INT NOT NULL,
					"slot" INT NOT NULL,
					"item_id" TEXT NOT NULL,
					
					PRIMARY KEY ("profile_id", "slot")
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
