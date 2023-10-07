package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class WeaponMerchantInventory implements InventoryHolderWithId {
	private final Inventory inventory;
	private final DeepAndDeeper plugin;

	public WeaponMerchantInventory(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54);

		this.plugin.itemManager.items.values().forEach(item -> {
			ItemStack itemStack = item.item().clone();

			// add another lore line to show price
			ItemMeta meta = itemStack.getItemMeta();
			Component price = Component.text(String.format("§fPrice: §6%d §ecoins", item.buyPrice()));

			if (meta.hasLore()) {
				List<Component> lore = meta.lore();

				lore.add(Component.space());
				lore.add(price);

				meta.lore(lore);
			} else {
				meta.lore(List.of(Component.space(), price));
			}

			itemStack.setItemMeta(meta);

			this.inventory.addItem(itemStack);
		});
	}

	@Override
	public UUID id() {
		return UUID.fromString("54c5177c-999a-40a5-ab4a-6ce4bcd19bce");
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

			statement.setInt(1, item.buyPrice());
			statement.setObject(2, event.getWhoClicked().getUniqueId());

			ResultSet result = statement.executeQuery();

			if (!result.next()) {
				event.getWhoClicked().sendMessage("§a§l$ §7You need to have a profile before purchasing anything.");
				return;
			}

			int coins = result.getInt("coins");

			if (coins < 0) {
				event.getWhoClicked().sendMessage(
					String.format("§a§l$ §7You need §6%d coins§7 to purchase %s§7.", item.buyPrice(), item.name().content()),
					String.format("§a§l$ §7You only have §6%d coins§7.", coins + item.buyPrice())
				);
				connection.rollback();

				return;
			}

			var remaining = event.getWhoClicked().getInventory().addItem(item.item());

			if (!remaining.isEmpty()) {
				event.getWhoClicked().sendMessage("§a§l$ §7You don't have enough space in your inventory.");
				connection.rollback();

				return;
			}

			event.getWhoClicked().sendMessage(String.format("§a§l$ §7You purchased %s §7for §6%d coins§7.", item.name().content(), item.buyPrice()));
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