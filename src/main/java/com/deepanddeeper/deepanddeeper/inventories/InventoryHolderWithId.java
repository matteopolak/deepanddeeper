package com.deepanddeeper.deepanddeeper.inventories;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;
import java.util.UUID;

public interface InventoryHolderWithId extends InventoryHolder {
	public UUID id();

	public void onInventoryClick(InventoryClickEvent event) throws SQLException;
}
