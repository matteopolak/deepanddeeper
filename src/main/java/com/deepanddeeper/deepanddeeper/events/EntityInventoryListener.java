package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.inventories.InventoryHolderWithId;
import com.deepanddeeper.deepanddeeper.inventories.WeaponMerchantInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class EntityInventoryListener implements Listener {
	private HashMap<UUID, InventoryHolder> inventories = new HashMap<>();

	public EntityInventoryListener(DeepAndDeeper plugin) {
		InventoryHolderWithId[] inventories = {
			new WeaponMerchantInventory(plugin),
		};

		for (InventoryHolderWithId inventory : inventories) {
			this.inventories.put(inventory.id(), inventory);
		}
	}

	@EventHandler
	public void onPlayerEntityClick(PlayerInteractEntityEvent event) {
		UUID entityId = event.getRightClicked().getUniqueId();
		InventoryHolder inventory = this.inventories.get(entityId);

		if (inventory == null) {
			return;
		}

		event.getPlayer().openInventory(inventory.getInventory());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		var inventory = event.getInventory();

		for (var entry : this.inventories.entrySet()) {
			if (entry.getValue().getInventory().equals(inventory)) {
				event.setCancelled(true);

				var clickedInventory = event.getClickedInventory();

				// If the clicked inventory is the player's, exit immediately
				if (clickedInventory == null || clickedInventory.getType() == InventoryType.PLAYER) {
					return;
				}

				((InventoryHolderWithId) entry.getValue()).onInventoryClick(event);

				return;
			}
		}
	}
}