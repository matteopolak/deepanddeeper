package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class SellMerchantInventory implements InventoryHolder {
	// itemstack with a name and lore, gold ingot material
	private static final int SELL_ITEM_SLOT = 49;

	private final DeepAndDeeper plugin;
	private int value = 0;
	private final Inventory inventory;

	public SellMerchantInventory(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54);

		this.value(0);
	}

	public void sell(HumanEntity player) throws SQLException {
		this.plugin.statisticsManager.addCoins(player, this.value);

		this.inventory.clear();
		this.value(0);
	}

	public void value(int value) {
		this.value = value;

		ItemStack sellItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = sellItem.getItemMeta();

		meta.displayName(Component.text("§b§lSELL ALL"));
		meta.lore(List.of(
			Component.text(String.format("§7Sell all items for §6%d coins§7.", this.value))
		));

		sellItem.setItemMeta(meta);

		this.inventory.setItem(SELL_ITEM_SLOT, sellItem);
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	public void addItem(@NotNull String itemId) {
		Item item = this.plugin.itemManager.item(itemId);

		this.value(this.value + item.sellPrice());
	}

	public void removeItem(int slot) {
		ItemStack itemStack = this.inventory.getItem(slot);

		if (itemStack == null) {
			return;
		}

		ItemMeta meta = itemStack.getItemMeta();
		String itemId = meta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);

		this.value(this.value - this.plugin.itemManager.item(itemId).sellPrice());
	}

	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		if (event.getSlot() == SELL_ITEM_SLOT) {
			event.setCancelled(true);

			if (this.value > 0) this.sell(event.getWhoClicked());

			return;
		}

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
					return;
				}

				ItemMeta currentItemMeta = currentItem == null ? null : currentItem.getItemMeta();
				String currentItemId = currentItemMeta == null ? null
					: currentItemMeta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);

				// add the item to the slot
				if (currentItemId != null) {
					this.addItem(currentItemId);
				} else if (currentItem != null) {
					event.setCancelled(true);
					event.getWhoClicked().sendMessage("§c§l> §7You can't sell that!");
				}
			}

			return;
		}

		ItemMeta cursorItemMeta = cursorItem.getItemMeta();

		String cursorItemId = cursorItemMeta == null ? null
			: cursorItemMeta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);

		// if the cursor item is air, the player is removing an item from the stash
		if (cursorItem.getType() == Material.AIR && currentItem != null) {
			this.removeItem(event.getSlot());
		} else if (cursorItemId != null) {
			this.addItem(cursorItemId);
		} else if (cursorItem.getType() != Material.AIR) {
			event.setCancelled(true);
			event.getWhoClicked().sendMessage("§c§l> §7You can't sell that!");
		}
	}
}
