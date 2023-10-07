package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager {
	private DeepAndDeeper plugin;

	public Map<UUID, GameClass> classes = new HashMap<>();

	public ClassManager(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public void activateClass(Player player, GameClassType type) {
		GameClass gameClass = type.getGameClass(this.plugin);

		gameClass.onActivate(player);
		this.classes.put(player.getUniqueId(), gameClass);
	}

	public void deactivateClass(Player player) {
		GameClass gameClass = this.classes.get(player.getUniqueId());

		if (gameClass != null) {
			gameClass.onDeactivate(player);
			this.classes.remove(player.getUniqueId());
		}
	}

	public boolean switchClass(Player player, GameClassType type) {
		GameClass gameClass = this.classes.get(player.getUniqueId());

		if (gameClass != null && gameClass.type() == type) {
			return false;
		}

		this.deactivateClass(player);
		this.activateClass(player, type);

		return true;
	}
}
