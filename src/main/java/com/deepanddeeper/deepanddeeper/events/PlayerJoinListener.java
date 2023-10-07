package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
	private final DeepAndDeeper plugin;

	public PlayerJoinListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();

		// Teleport the player to the lobby
		player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5, 0, 0));

		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		player.setFoodLevel(20);

		// Remove the player from the "playing" team when they join
		boolean wasInGame = this.plugin.playingTeam.removePlayer(player);

		if (wasInGame) {
			// now using colour escape codes
			player.sendMessage("§c§l> §7You quit while in a game and were put back in the lobby.");
		}

		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement insertUser = connection.prepareStatement("""
					INSERT INTO "user" ("uuid") VALUES (?);
				""");
			PreparedStatement insertProfile = connection.prepareStatement("""
					INSERT INTO "profile" ("user", "active") VALUES (?, TRUE);
				""");
		) {
			player.sendMessage("§b§l> §7Checking if you have a profile...");

			connection.setAutoCommit(false);

			insertUser.setObject(1, player.getUniqueId());
			insertUser.execute();

			insertProfile.setObject(1, player.getUniqueId());
			insertProfile.execute();

			connection.commit();
			player.sendMessage("§a§l> §7Your profile has been created!");

			this.plugin.getLogger().info("Created user and profile for " + player.getName());
		} catch (SQLException e) {
			player.sendMessage("§b§l> §7You already have a profile, welcome back!");

			// If the user already exists, ignore the error
			if (e.getErrorCode() != 0) {
				e.printStackTrace();
			}
		} finally {
			connection.setAutoCommit(true);
		}

		int coins = this.plugin.statisticsManager.getCoins(player);

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = scoreboard.getObjective("coins");

		if (objective == null) {
			objective = scoreboard.registerNewObjective("coins", Criteria.DUMMY, Component.text("Coins"));
		}

		// display coins on scoreboard only to the player, like FeatherBoard
		objective.getScore(player.getName()).setScore(coins);
	}
}