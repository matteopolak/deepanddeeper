package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClass;
import com.deepanddeeper.deepanddeeper.game.Game;
import com.deepanddeeper.deepanddeeper.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;

public class GameEventListener implements Listener {
	private DeepAndDeeper plugin;

	public GameEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	// on player death
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game == null || game.hasEnded()) {
			return;
		}

		this.plugin.statisticsManager.addDeath(player);

		game.removePlayer(player);
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());

		Player killer = player.getKiller();

		if (killer != null) {
			this.plugin.statisticsManager.addKill(killer);
			game.sendMessage(String.format("§c§l> §f%s §7was killed by §f%s§7.", player.getName(), killer.getName()));
		} else {
			game.sendMessage(String.format("§c§l> §f%s §7has died!", player.getName()));
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());
			Inventory inventory = event.getClickedInventory();

			// disable modifying the hotbar when a class is equipped
			if (gameClass != null && inventory != null && inventory.getType() == InventoryType.PLAYER && !gameClass.canModifySlot(event.getSlot())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());
		int slot = player.getInventory().getHeldItemSlot();

		if (gameClass != null && !gameClass.canModifySlot(slot)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game != null) {
			player.setHealth(0);

			game.sendMessage(String.format("§c§l> §f%s §7has left the game!", player.getName()));
			game.removePlayer(player);
		}
	}
}
