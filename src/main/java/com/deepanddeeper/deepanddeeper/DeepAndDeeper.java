package com.deepanddeeper.deepanddeeper;


import com.deepanddeeper.deepanddeeper.commands.GetWorldCommand;
import com.deepanddeeper.deepanddeeper.events.EntityClickListener;
import com.deepanddeeper.deepanddeeper.events.PlayerJoinListener;
import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import com.deepanddeeper.deepanddeeper.weapons.WeaponHolder;
import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class DeepAndDeeper extends JavaPlugin {
	private FileConfiguration config = this.getConfig();
	private Database database;

	public WeaponHolder weaponHolder;

	private void registerListeners() {
		// Add event listeners here

		Listener[] listeners = {
			new PlayerJoinListener(this),
			new EntityClickListener(),
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
		};

		for (CommandWithName command : commands) {
			this.getCommand(command.commandName()).setExecutor(command);
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, "Hello, world!");

		this.saveDefaultConfig();

		this.weaponHolder = new WeaponHolder(((List<Object>) this.getConfig().getList("weapons")).stream()
			.map(weapon -> Weapon.deserialize((Map<String, Object>) weapon))
			.toList());

		this.registerCommands();
		this.registerListeners();

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
