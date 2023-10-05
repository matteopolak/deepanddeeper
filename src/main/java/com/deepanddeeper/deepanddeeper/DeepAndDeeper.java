package com.deepanddeeper.deepanddeeper;


import com.deepanddeeper.deepanddeeper.commands.GetWorldCommand;
import com.deepanddeeper.deepanddeeper.commands.party.PartyAcceptCommand;
import com.deepanddeeper.deepanddeeper.commands.party.PartyInviteCommand;
import com.deepanddeeper.deepanddeeper.commands.party.PartyKickCommand;
import com.deepanddeeper.deepanddeeper.events.*;
import com.deepanddeeper.deepanddeeper.game.GameManager;
import com.deepanddeeper.deepanddeeper.game.StatisticsManager;
import com.deepanddeeper.deepanddeeper.items.ItemManager;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import com.deepanddeeper.deepanddeeper.party.PartyManager;

import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public final class DeepAndDeeper extends JavaPlugin {
	private FileConfiguration config = this.getConfig();
	public Database database;

	public ItemManager itemManager = new ItemManager(this);
	public PartyManager partyManager = new PartyManager();
	public GameManager gameManager = new GameManager(this);
	public StatisticsManager statisticsManager = new StatisticsManager(this);

	private Scoreboard scoreboard;
	public Team playingTeam;

	private void registerListeners() {
		// Add event listeners here

		Listener[] listeners = {
			new PartyEventListener(this),
			new PlayerJoinListener(this),
			new EntityClickListener(this),
			new GameEventListener(this),
			new WizardEventListener(this),
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
			new PartyInviteCommand(this),
			new PartyAcceptCommand(this),
			new PartyKickCommand(this),
		};

		for (CommandWithName command : commands) {
			this.getCommand(command.commandName()).setExecutor(command);
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getWorld("world").setSpawnLocation(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5, 0, 0));

		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		try {
			this.playingTeam = this.scoreboard.registerNewTeam("playing");
			this.playingTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		} catch (IllegalArgumentException e) {
			// The team already exists
			this.playingTeam = this.scoreboard.getTeam("playing");
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		this.saveDefaultConfig();

		((List<Object>) this.getConfig().getList("weapons")).stream()
				.map(weapon -> Weapon.deserialize(this, (Map<String, Object>) weapon))
				.forEach(w -> {
					this.itemManager.registerItem(w);
					this.getLogger().info("Registered weapon " + w.name());
				});

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
