package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GeneralEventListener implements Listener {
	private DeepAndDeeper plugin;

	public GeneralEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		// get food level change
		if (event.getEntity() instanceof Player player) {
			if (this.plugin.gameManager.isInGame(player)) {
				int previous = player.getFoodLevel();
				int current = event.getFoodLevel();

				if (current > previous) {
					// increase the player's health by half of the increase in food level
					player.setHealth(player.getHealth() + (current - previous) / 2.0);
				}
			}

			event.setCancelled(true);
		}
	}
}
