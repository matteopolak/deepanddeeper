package com.deepanddeeper.deepanddeeper.items.weapons;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.inventories.SpellSelectorInventory;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SpellSelector extends Weapon {
	private final SpellSelectorInventory inventory;

	public SpellSelector(DeepAndDeeper plugin, String id, int buyPrice, int sellPrice, String name, Material material, List<String> lore) {
		super(plugin, id, buyPrice, sellPrice, name, material, lore, 0);

		this.inventory = new SpellSelectorInventory(plugin);
	}

	@Override
	public boolean canActivate() {
		return true;
	}

	@Override
	public boolean onActivate(PlayerInteractEvent event) {
		if (!super.onActivate(event)) return false;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.getPlayer().openInventory(this.inventory.getInventory());
		}

		return true;
	}
}
