package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.inventories.PlayerStashInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;
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
		return UUID.fromString("c8daefdf-5daf-41ed-8109-e0a2995519fa");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) {
		InventoryHolder holder = new PlayerStashInventory(this.plugin, event.getPlayer().getUniqueId());

		event.getPlayer()
			.openInventory(holder.getInventory());
	}
}