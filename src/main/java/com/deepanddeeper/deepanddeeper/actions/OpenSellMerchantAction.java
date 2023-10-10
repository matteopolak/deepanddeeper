package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.inventories.SellMerchantInventory;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class OpenSellMerchantAction implements Action {
	private DeepAndDeeper plugin;

	public OpenSellMerchantAction(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@Override
	public UUID id() {
		return UUID.fromString("f0c13dff-8e91-46c5-9873-c49be7e3e775");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) {
		InventoryHolder holder = new SellMerchantInventory(this.plugin);

		event.getPlayer()
			.openInventory(holder.getInventory());
	}
}
