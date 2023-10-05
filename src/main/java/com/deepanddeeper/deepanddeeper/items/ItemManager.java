package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemManager {
	private DeepAndDeeper plugin;

	public NamespacedKey idKey;
	public Map<String, Item> items;

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
		return this.items.get(id);
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
