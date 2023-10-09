package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemManager {
	public NamespacedKey idKey;
	public Map<String, Item> items;
	private DeepAndDeeper plugin;
	private Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

	public ItemManager(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.idKey = new NamespacedKey(this.plugin, "id");
		this.items = new HashMap<>();
	}

	public ItemManager(Map<String, Item> items) {
		this.items = items;
	}

	public void registerItem(Item item) {
		this.items.put(item.id(), item);
	}

	public Item item(String id) {
		Bukkit.getLogger().info("ItemManager.item: " + id + " " + this.items.containsKey(id));

		return this.items.get(id);
	}

	public Item item(ItemStack item) {
		if (item == null) return null;

		String itemId = item.getItemMeta().getPersistentDataContainer().get(this.idKey, PersistentDataType.STRING);
		if (itemId == null) return null;

		return this.item(itemId);
	}

	public long remainingCooldown(Player player, Weapon weapon) {
		long currentTime = System.currentTimeMillis();
		var cooldowns = this.cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

		Long lastUsed = cooldowns.get(weapon.id());

		if (lastUsed == null || currentTime - lastUsed > weapon.cooldown()) {
			cooldowns.put(weapon.id(), currentTime);

			return 0;
		}

		return weapon.cooldown() - (currentTime - lastUsed);
	}
}
