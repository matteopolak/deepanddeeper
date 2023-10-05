package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WizardEventListener implements Listener {
	private DeepAndDeeper plugin;

	public WizardEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item == null) return;

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		String itemId = meta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);
		if (itemId == null || !itemId.equals("wizard_fireball")) return;

		if (this.plugin.itemManager.item(itemId) instanceof Weapon weapon) {
			long cooldown = this.plugin.itemManager.remainingCooldown(event.getPlayer(), weapon);

			if (cooldown > 0) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(String.format("§c§l> §f%s §7is on cooldown for §f%,.1f seconds§7.", weapon.name().content(), ((double) cooldown) / 1_000));

				return;
			}

			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				// launch a fireball
				event.getPlayer().launchProjectile(Fireball.class);
			}
		}
	}
}
