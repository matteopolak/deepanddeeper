package com.deepanddeeper.deepanddeeper.inventories;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClass;
import com.deepanddeeper.deepanddeeper.classes.WizardClass;
import com.deepanddeeper.deepanddeeper.classes.WizardSpell;
import com.deepanddeeper.deepanddeeper.items.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SpellSelectorInventory implements InventoryHolder {
	private final Inventory inventory;
	private final DeepAndDeeper plugin;

	public SpellSelectorInventory(DeepAndDeeper plugin) {
		this.inventory = Bukkit.createInventory(this, 27, Component.text("Select a spell..."));
		this.plugin = plugin;

		this.inventory.setItem(11, this.plugin.itemManager.item("wizard_fireball").item());
		this.inventory.setItem(12, this.plugin.itemManager.item("wizard_lightning").item());
		this.inventory.setItem(14, this.plugin.itemManager.item("wizard_invisibility").item());
		this.inventory.setItem(15, this.plugin.itemManager.item("wizard_haste").item());
	}

	@Override
	public @NotNull Inventory getInventory() {
		return this.inventory;
	}

	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);

		String itemId = switch (event.getSlot()) {
			case 11 -> "wizard_fireball";
			case 12 -> "wizard_lightning";
			case 14 -> "wizard_invisibility";
			case 15 -> "wizard_haste";
			default -> null;
		};

		if (itemId == null) return;

		Item item = this.plugin.itemManager.item(itemId);
		if (item == null) return;

		HumanEntity player = event.getWhoClicked();

		GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());
		if (gameClass == null) return;

		if (gameClass instanceof WizardClass wizardClass) {
			wizardClass.spell(switch (item.id()) {
				case "wizard_fireball" -> WizardSpell.FIREBALL;
				case "wizard_lightning" -> WizardSpell.LIGHTNING;
				case "wizard_invisibility" -> WizardSpell.INVISIBILITY;
				case "wizard_haste" -> WizardSpell.HASTE;
				default -> null;
			});

			player.closeInventory();
			player.sendMessage(String.format("§a§l> §7You have selected the %s §7spell.", item.name().content()));
		}
	}
}
