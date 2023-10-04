package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
	private DeepAndDeeper plugin;

	public PlayerJoinListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();

		// Teleport the player to the lobby
		player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5, 0, 0));

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
	}
}