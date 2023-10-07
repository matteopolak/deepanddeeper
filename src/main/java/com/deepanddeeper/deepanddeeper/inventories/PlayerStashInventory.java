package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerStashInventory implements InventoryHolder {
	private final Inventory inventory;
	private final DeepAndDeeper plugin;
	private int profileId;

	public PlayerStashInventory(DeepAndDeeper plugin, UUID playerId) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54);

		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement getProfileIdStatement = this.plugin.database.getConnection().prepareStatement("""
					SELECT "id" FROM "profile" WHERE "user" = ? AND "active" = TRUE;
				""");
			PreparedStatement getItemsStatement = connection.prepareStatement("""
					SELECT "item_id", "slot" FROM "stash" WHERE "profile_id" = ? ORDER BY "slot" ASC;
				""");
		) {
			getProfileIdStatement.setObject(1, playerId);

			var profileIdResultSet = getProfileIdStatement.executeQuery();

			if (!profileIdResultSet.next()) {
				return;
			}

			int profileId = profileIdResultSet.getInt("id");
			this.profileId = profileId;

			getItemsStatement.setInt(1, profileId);

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

	public void addItemToStash(@NotNull String itemId, int slot) {
		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement addItemStatement = connection.prepareStatement("""
					INSERT INTO "stash" ("profile_id", "item_id", "slot") VALUES (?, ?, ?)
						ON CONFLICT ("profile_id", "slot") DO UPDATE SET "item_id" = excluded."item_id";
				""");
		) {
			ItemStack item = this.plugin.itemManager.item(itemId).item();

			addItemStatement.setInt(1, this.profileId);
			addItemStatement.setString(2, itemId);
			addItemStatement.setInt(3, slot);

			addItemStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeItemFromStash(int slot) {
		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement removeItemStatement = connection.prepareStatement("""
					DELETE FROM "stash" WHERE "profile_id" = ? AND "slot" = ?;
				""");
		) {
			removeItemStatement.setInt(1, this.profileId);
			removeItemStatement.setInt(2, slot);

			removeItemStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// if the clicked slot is the stash, add the item to the stash
	// and remove the item that was in its place from the stash
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack currentItem = event.getCurrentItem();
		// cannot be null
		ItemStack cursorItem = event.getCursor();
		Inventory clickedInventory = event.getClickedInventory();

		// if the clicked inventory is the player's inventory,
		// continue doing what the player was doing
		if (clickedInventory == null || clickedInventory.getType() == InventoryType.PLAYER) {
			if (event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT)) {
				// get the slot the item was moved to
				// we assume all items are moved to the first empty slot
				// since stackables will not be moved to the stash
				int slot = inventory.firstEmpty();

				if (slot == -1) {
					event.setCancelled(true);
					event.getWhoClicked().sendMessage("§c§l> §7Your stash is full!");
					return;
				}

				ItemMeta currentItemMeta = currentItem == null ? null : currentItem.getItemMeta();
				String currentItemId = currentItemMeta == null ? null
					: currentItemMeta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);

				// add the item to the slot
				if (currentItemId != null) {
					this.addItemToStash(currentItemId, slot);
				} else if (currentItem != null) {
					event.setCancelled(true);
					event.getWhoClicked().sendMessage("§c§l> §7You can't put that in your stash!");
				}
			}

			return;
		}

		ItemMeta cursorItemMeta = cursorItem.getItemMeta();


		String cursorItemId = cursorItemMeta == null ? null
			: cursorItemMeta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);

		// if the cursor item is air, the player is removing an item from the stash
		if (cursorItem.getType() == Material.AIR && currentItem != null) {
			this.removeItemFromStash(event.getSlot());
		} else if (cursorItemId != null) {
			this.addItemToStash(cursorItemId, event.getSlot());
		} else if (cursorItem.getType() != Material.AIR) {
			event.setCancelled(true);
			event.getWhoClicked().sendMessage("§c§l> §7You can't put that in your stash!");
		}
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}