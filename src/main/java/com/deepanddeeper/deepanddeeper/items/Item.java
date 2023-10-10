package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Item {
	protected ItemStack item;
	protected TextComponent name = Component.text("Name");
	protected Material material = Material.DIAMOND_SWORD;
	protected List<TextComponent> lore;
	protected int amount = 1;
	protected String id;
	protected int buyPrice;
	protected int sellPrice;

	protected DeepAndDeeper plugin;

	public Item(DeepAndDeeper plugin, String id, int buyPrice, int sellPrice, String name, Material material, int amount, List<String> lore) {
		this.plugin = plugin;
		this.id = id;
		this.name = Component.text(name);
		this.material = material;
		this.amount = amount;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.lore = lore
			.stream()
			.map(l -> l == null ? Component.text("") : Component.text(l))
			.toList();

		this.item = new ItemStack(this.material, this.amount);

		ItemMeta meta = item.getItemMeta();

		meta.displayName(this.name);
		meta.lore(this.lore);
		meta
			.getPersistentDataContainer()
			.set(plugin.itemManager.idKey, PersistentDataType.STRING, this.id());

		if (this.item.getType().getMaxDurability() > 0) {
			meta.setUnbreakable(true);
		}

		this.item.setItemMeta(meta);
	}

	public ItemStack item() {
		return this.item;
	}

	public Item name(TextComponent name) {
		this.name = name;
		return this;
	}

	public TextComponent name() {
		return this.name;
	}

	public Item lore(List<TextComponent> lore) {
		this.lore = lore;
		return this;
	}

	public String id() {
		return this.id;
	}

	public int buyPrice() {
		return this.buyPrice;
	}

	public int sellPrice() {
		return this.sellPrice;
	}
}
