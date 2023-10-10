package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClass;
import com.deepanddeeper.deepanddeeper.items.Item;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ClassEventListener implements Listener {
	private final DeepAndDeeper plugin;

	public ClassEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item == null) return;

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		String itemId = meta.getPersistentDataContainer().get(this.plugin.itemManager.idKey, PersistentDataType.STRING);
		if (itemId == null) return;

		if (
			(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& this.plugin.itemManager.item(itemId) instanceof Weapon weapon) {
			if (weapon.canActivate()) {
				event.setCancelled(true);
				weapon.onActivate(event);
			}
		}
	}

	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player attacker) {
			if (event.getEntity() instanceof Player victim) {
				Item item = this.plugin.itemManager.item(attacker.getInventory().getItemInMainHand());

				if (item instanceof Weapon weapon) {
					weapon.onHit(event, attacker, victim);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());

		if (gameClass != null) {
			gameClass.onActivate(player);
		}
	}
}
