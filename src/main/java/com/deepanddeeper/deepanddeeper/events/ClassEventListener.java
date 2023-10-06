package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.Weapon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ClassEventListener implements Listener {
	private DeepAndDeeper plugin;

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

		if (this.plugin.itemManager.item(itemId) instanceof Weapon weapon) {
			if (weapon.canActivate()) {
				event.setCancelled(true);
				weapon.onActivate(event);
			}
		}
	}
}
