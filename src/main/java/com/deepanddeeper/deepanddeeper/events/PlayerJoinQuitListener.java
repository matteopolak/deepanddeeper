package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClassType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerJoinQuitListener implements Listener {
	private final DeepAndDeeper plugin;

	public PlayerJoinQuitListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.quitMessage(null);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		event.joinMessage(null);

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
					INSERT INTO "user" ("uuid") VALUES (?) ON CONFLICT DO NOTHING;
				""");
			PreparedStatement getActiveProfile = connection.prepareStatement("""
					SELECT "class" FROM "profile" WHERE "user" = ? AND "active" = TRUE;
				""")
		) {
			player.sendMessage("§b§l> §7Checking if you have a profile...");

			connection.setAutoCommit(false);

			insertUser.setObject(1, player.getUniqueId());
			insertUser.execute();

			getActiveProfile.setObject(1, player.getUniqueId());

			var activeProfileResultSet = getActiveProfile.executeQuery();

			if (activeProfileResultSet.next()) {
				var classId = activeProfileResultSet.getInt("class");
				GameClassType gameClassType = GameClassType.fromId(classId);

				this.plugin.classManager.blankActivateClass(player, gameClassType);
				player.sendMessage(String.format("§a§l> §7Welcome back! You are a §f%s§7.", gameClassType.colouredName()));

				return;
			}

			connection.commit();
		} catch (SQLException e) {
			player.sendMessage("§c§l> §7An error occurred while loading your profile. Please try again later.");

			// If the user already exists, ignore the error
			if (e.getErrorCode() != 0) {
				e.printStackTrace();
			}
		} finally {
			connection.setAutoCommit(true);
		}


		//Initialize Scoreboard
		Scoreboard scoreboard = player.getScoreboard();
		plugin.scoreboardManager.update(scoreboard);


	}
}