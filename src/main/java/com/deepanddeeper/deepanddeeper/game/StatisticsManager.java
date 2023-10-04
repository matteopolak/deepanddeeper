package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatisticsManager {
	private DeepAndDeeper plugin;

	public StatisticsManager(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public void addKill(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "kills" = "kills" + 1 WHERE "uuid" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addDeath(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "deaths" = "deaths" + 1 WHERE "uuid" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addWin(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "wins" = "wins" + 1 WHERE "uuid" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addLoss(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "losses" = "losses" + 1 WHERE "uuid" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}
}
