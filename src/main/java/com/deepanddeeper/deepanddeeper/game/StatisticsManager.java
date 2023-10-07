package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsManager {
	private DeepAndDeeper plugin;

	public StatisticsManager(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public int getCoins(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			SELECT "coins" FROM "profile" WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());

		ResultSet result = statement.executeQuery();

		if (!result.next()) {
			return 0;
		}

		return result.getInt("coins");
	}

	public void addCoins(HumanEntity player, int coins) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "coins" = "coins" + ? WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setInt(1, coins);
		statement.setObject(2, player.getUniqueId());

		statement.execute();

		player.sendMessage(String.format("§a§l$ §7You received §6%d coins§7.", coins));
	}

	public void addKill(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "kills" = "kills" + 1 WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addDeath(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "deaths" = "deaths" + 1 WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addWin(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "wins" = "wins" + 1 WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}

	public void addLoss(Player player) throws SQLException {
		Connection connection = this.plugin.database.getConnection();

		PreparedStatement statement = connection.prepareStatement("""
			UPDATE "profile" SET "losses" = "losses" + 1 WHERE "user" = ? AND "active" = TRUE;
		""");

		statement.setObject(1, player.getUniqueId());
		statement.executeUpdate();
	}
}
