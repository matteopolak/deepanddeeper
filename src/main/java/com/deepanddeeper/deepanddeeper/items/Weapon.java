package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.weapons.SpellSelector;
import com.deepanddeeper.deepanddeeper.items.weapons.WizardStaff;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Weapon extends Item {
	private double damage = 1;
	private long cooldown = 1_200;

	public Weapon(DeepAndDeeper plugin, String id, int buyPrice, int sellPrice, String name, Material material, List<String> lore, double cooldown) {
		super(plugin, id, buyPrice, sellPrice, name, material, 1, lore, cooldown);

	}

	public static @NotNull Weapon deserialize(DeepAndDeeper plugin, Map<String, Object> data) {
		String id = (String) data.get("id");
		String name = (String) data.get("name");
		List<String> lore = (List<String>) data.get("lore");
		double damage = (double) data.get("damage");
		double cooldown = (double) data.get("cooldown");
		Material material = Material.valueOf((String) data.get("material"));
		int buyPrice = (int) data.get("buy_price");
		int sellPrice = (int) data.get("sell_price");

		return (switch (id) {
			case "wizard_staff" -> new WizardStaff(plugin, id, buyPrice, sellPrice, name, material, lore);
			case "wizard_spell_selector" -> new SpellSelector(plugin, id, buyPrice, sellPrice, name, material, lore);
			default -> new Weapon(plugin, id, buyPrice, sellPrice, name, material, lore, cooldown);
		})
			.damage(damage)
			.cooldown((long) (cooldown * 1000d));
	}

	public Weapon damage(double damage) {
		this.damage = damage;
		return this;
	}

	public Weapon cooldown(long cooldown) {
		this.cooldown = cooldown;
		return this;
	}

	public long cooldown() {
		return this.cooldown;
	}

	public void onHit(EntityDamageByEntityEvent event, Player attacker, Player victim) {
		event.setDamage(this.damage);
	}

	public boolean canActivate() {
		return false;
	}

	public boolean onActivate(PlayerInteractEvent event) {
		long cooldown = this.plugin.itemManager.remainingCooldown(event.getPlayer(), this);

		if (cooldown > 0) {
			event.getPlayer().sendMessage(String.format("§c§l> §f%s §7is on cooldown for §f%,.1f seconds§7.", this.name().content(), ((double) cooldown) / 1_000));

			return false;
		}

		return true;
	}
}
