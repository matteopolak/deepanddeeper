package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.inventories.PlayerStashInventory;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class OpenStashAction implements Action {
	private final DeepAndDeeper plugin;

	public OpenStashAction(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@Override
	public UUID id() {
		return UUID.fromString("79295d34-3120-4b0b-bfab-d301bfebb3b5");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) {
		InventoryHolder holder = new PlayerStashInventory(this.plugin, event.getPlayer().getUniqueId());

		event.getPlayer()
			.openInventory(holder.getInventory());
	}
}
