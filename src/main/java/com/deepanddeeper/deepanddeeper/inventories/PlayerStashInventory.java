package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerStashInventory implements InventoryHolderWithId {
	private Inventory inventory;
	private DeepAndDeeper plugin;

	public PlayerStashInventory(DeepAndDeeper plugin, UUID playerId) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54);

		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement getProfileIdStatement = this.plugin.database.getConnection().prepareStatement("""
				SELECT "id" FROM "profiles" WHERE "user" = ? AND "active" = TRUE;
			""");
			PreparedStatement getItemsStatement = connection.prepareStatement("""
				SELECT "item_id", "slot" FROM "stash" WHERE "user" = ? AND "profile_id" = ? ORDER BY "slot" ASC;
			""");
			) {
			getProfileIdStatement.setObject(1, playerId);

			var profileIdResultSet = getProfileIdStatement.executeQuery();

			if (!profileIdResultSet.next()) {
				return;
			}

			int profileId = profileIdResultSet.getInt("id");

			getItemsStatement.setObject(1, playerId);
			getItemsStatement.setInt(2, profileId);

			var itemsResultSet = getItemsStatement.executeQuery();

			while (itemsResultSet.next()) {
				int slot = itemsResultSet.getInt("slot");
				String itemId = itemsResultSet.getString("item_id");
				ItemStack item = this.plugin.itemManager.item(itemId).item();

				this.inventory.setItem(slot, item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public UUID id() {
		return UUID.fromString("2dceb724-dd79-43c3-8400-6f404480c99b");
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		ItemStack clickedItem = event.getCurrentItem();

		if (clickedItem != null && clickedItem.getType() == Material.IRON_SWORD) {
			Connection connection = this.plugin.database.getConnection();

			try (
				PreparedStatement statement = connection.prepareStatement("""
					UPDATE "profile" SET "coins" = "coins" - 400 WHERE "user" = ? AND "active" = TRUE RETURNING "coins";
				""")) {
				connection.setAutoCommit(false);

				statement.setObject(1, event.getWhoClicked().getUniqueId());

				ResultSet result = statement.executeQuery();

				if (!result.next()) {
					event.getWhoClicked().sendMessage("§a§l$ §7You need to have a profile before purchasing anything.");
					return;
				}

				int coins = result.getInt("coins");

				if (coins < 0) {
					event.getWhoClicked().sendMessage(
						"§a§l$ §7You need §6400 coins§7 to purchase this item.",
						"§a§l$ §7You only have §6" + (coins + 400) + " coins§7."
					);
					connection.rollback();

					return;
				}

				var remaining = event.getWhoClicked().getInventory().addItem(clickedItem);

				if (!remaining.isEmpty()) {
					event.getWhoClicked().sendMessage("§a§l$ §7You don't have enough space in your inventory.");
					connection.rollback();

					return;
				}

				event.getWhoClicked().sendMessage("§a§l$ §7You purchased an iron sword for §6400 coins§7.");
				connection.commit();

			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}