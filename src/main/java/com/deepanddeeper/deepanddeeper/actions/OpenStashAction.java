package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class OpenStashAction implements Action {
	private DeepAndDeeper plugin;
	private NamespacedKey itemId;

	public OpenStashAction(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.itemId = new NamespacedKey(plugin, "itemId");
	}

	@Override
	public UUID id() {
		return UUID.fromString("53d4513c-20c9-48d9-abcc-314305e1a891");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) throws SQLException {
		PreparedStatement getProfileIdStatement = this.plugin.database.getConnection().prepareStatement("""
			SELECT "id" FROM "profiles" WHERE "user" = ? AND "active" = TRUE;
		""");

		getProfileIdStatement.setObject(1, event.getPlayer().getUniqueId());

		var profileIdResultSet = getProfileIdStatement.executeQuery();

		if (!profileIdResultSet.next()) {
			event.getPlayer().sendMessage("§c§l> §7You do not have an active profile!");
			return;
		}

		int profileId = profileIdResultSet.getInt("id");

		// open the player's stash from the database, selecting items within slots [0, 53]
		PreparedStatement statement = this.plugin.database.getConnection().prepareStatement("""
			SELECT "item_id" FROM "stash" WHERE "user" = ? AND "slot" BETWEEN 0 AND 53 AND "profile" = ? ORDER BY "slot" ASC;
		""");

		statement.setObject(1, event.getPlayer().getUniqueId());

		// get the result set
		var resultSet = statement.executeQuery();

		// create a new inventory
		var inventory = Bukkit.createInventory(null, 54, Component.text("Stash"));
	}
}
