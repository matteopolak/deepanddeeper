package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
	private DeepAndDeeper plugin;

	public NamespacedKey idKey;
	public Map<String, Item> items;

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
}
