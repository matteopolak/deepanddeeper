package com.deepanddeeper.deepanddeeper.items.weapons;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Lightning extends Weapon {
	public Lightning(DeepAndDeeper plugin, String id, int price, String name, Material material, List<String> lore) {
		super(plugin, id, price, name, material, lore);
	}

	@Override
	public boolean canActivate() {
		return true;
	}

	@Override
	public boolean onActivate(PlayerInteractEvent event) {
		if (!super.onActivate(event)) return false;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// spawn lightning at the location the player is looking at, 10 blocks away at most
			event.getPlayer().getWorld().strikeLightning(event.getPlayer().getTargetBlock(null, 10).getLocation());
		}

		return true;
	}
}
