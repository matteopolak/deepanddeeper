package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WeaponMerchantInventory implements InventoryHolderWithId {
	private Inventory inventory;
	private DeepAndDeeper plugin;

	public WeaponMerchantInventory(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54);

		this.plugin.itemManager.items.values().forEach(item -> {
			ItemStack itemStack = item.item();

			// add another lore line to show price
			ItemMeta meta = itemStack.getItemMeta();
			Component price = Component.text(String.format("§fPrice: §6%d §ecoins", item.price()));

			if (meta.hasLore()) {
				List<Component> lore = meta.lore();

				lore.add(Component.space());
				lore.add(price);

				meta.lore(lore);
			} else {
				meta.lore(List.of(Component.space(), price));
			}

			meta.getPersistentDataContainer()
				.set(plugin.itemManager.idKey, PersistentDataType.STRING, item.id());

			itemStack.setItemMeta(meta);

			this.inventory.addItem(itemStack);
		});
	}

	@Override
	public UUID id() {
		return UUID.fromString("2dceb724-dd79-43c3-8400-6f404480c99b");
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		ItemStack clickedItem = event.getCurrentItem();
		if (clickedItem == null) return;

		String itemId = clickedItem.getItemMeta().getPersistentDataContainer().get(plugin.itemManager.idKey, PersistentDataType.STRING);
		if (itemId == null) return;

		Item item = this.plugin.itemManager.item(itemId);
		if (item == null) return;

		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement statement = connection.prepareStatement("""
				UPDATE "profile" SET "coins" = "coins" - ? WHERE "user" = ? AND "active" = TRUE RETURNING "coins";
			""")) {
			connection.setAutoCommit(false);

			statement.setInt(1, item.price());
			statement.setObject(2, event.getWhoClicked().getUniqueId());

			ResultSet result = statement.executeQuery();

			if (!result.next()) {
				event.getWhoClicked().sendMessage("§a§l$ §7You need to have a profile before purchasing anything.");
				return;
			}

			int coins = result.getInt("coins");

			if (coins < 0) {
				event.getWhoClicked().sendMessage(
					String.format("§a§l$ §7You need §6%d coins§7 to purchase %s§7.", item.price(), item.name().content()),
					String.format("§a§l$ §7You only have §6%d coins§7.", coins + item.price())
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

			event.getWhoClicked().sendMessage(String.format("§a§l$ §7You purchased %s §7for §6%d coins§7.", item.name().content(), item.price()));
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(true);
		}
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}