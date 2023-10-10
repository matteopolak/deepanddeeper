package com.deepanddeeper.deepanddeeper.items.weapons;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClass;
import com.deepanddeeper.deepanddeeper.classes.WizardClass;
import com.deepanddeeper.deepanddeeper.classes.WizardSpell;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class WizardStaff extends Weapon {
	public WizardStaff(DeepAndDeeper plugin, String id, int buyPrice, int sellPrice, String name, Material material, List<String> lore) {
		super(plugin, id, buyPrice, sellPrice, name, material, lore, 5);
	}

	@Override
	public boolean canActivate() {
		return true;
	}

	@Override
	public boolean onActivate(PlayerInteractEvent event) {
		if (!super.onActivate(event)) return false;

		GameClass gameClass = this.plugin.classManager.classes.get(event.getPlayer().getUniqueId());

		if (gameClass instanceof WizardClass wizardClass) {
			WizardSpell spell = wizardClass.spell();

			if (spell == null) {
				event.getPlayer().sendMessage("§c§l> §7You haven't selected a spell yet.");
				return true;
			}

			switch (spell) {
				case FIREBALL -> event.getPlayer().launchProjectile(Fireball.class);
				case HASTE -> event.getPlayer()
					.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, 1));
				case INVISIBILITY -> event.getPlayer()
					.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3 * 20, 0));
				case LIGHTNING ->
					event.getPlayer().getWorld().strikeLightning(event.getPlayer().getTargetBlock(null, 5).getLocation());
			}
		}

		return true;
	}
}
