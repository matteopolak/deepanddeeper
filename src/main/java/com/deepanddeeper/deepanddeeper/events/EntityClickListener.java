package com.deepanddeeper.deepanddeeper.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EntityClickListener implements Listener, InventoryHolder {
	private Inventory inventory;

	public EntityClickListener() {
		this.inventory = Bukkit.createInventory(this, 54);
		this.inventory.setItem(0, new ItemStack(Material.IRON_SWORD));
	}

	@EventHandler
	public void onPlayerEntityClick(PlayerInteractEntityEvent event) {
		UUID entityId = event.getRightClicked().getUniqueId();

		if (entityId.equals(UUID.fromString("1e0067eb-07d2-4afe-8191-7f718bd12c21"))) {
			event.getPlayer().openInventory(this.inventory);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		var inventory = event.getInventory();

		// If the "opened" inventory isn't this one, abort
		if (!(inventory.getHolder(false) instanceof EntityClickListener)) {
			return;
		}

		event.setCancelled(true);

		var clickedInventory = event.getClickedInventory();

		// If the clicked inventory is the player's, exit immediately
		if (clickedInventory == null || clickedInventory.getType() == InventoryType.PLAYER) {
			return;
		}

		ItemStack clickedItem = event.getCurrentItem();

		if (clickedItem != null && clickedItem.getType() == Material.IRON_SWORD) {
			event.getWhoClicked().sendMessage("you bought an iron sword for 400 coins");

			var remaining = event.getWhoClicked().getInventory().addItem(clickedItem);

			if (!remaining.isEmpty()) {
				event.getWhoClicked().sendMessage("your inventory is full, so the item was not purchased");
			}
		}
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}