package com.deepanddeeper.deepanddeeper.items.weapons;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Haste extends Weapon {
	public Haste(DeepAndDeeper plugin, String id, int price, String name, Material material, List<String> lore) {
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
			// apply haste effect for 5 seconds to the player
			event.getPlayer()
				.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, 1));
		}

		return true;
	}
}
