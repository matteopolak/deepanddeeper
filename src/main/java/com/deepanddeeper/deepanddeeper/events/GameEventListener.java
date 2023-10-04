package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.game.Game;
import com.deepanddeeper.deepanddeeper.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class GameEventListener implements Listener {
	private DeepAndDeeper plugin;

	public GameEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	// on player death
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
		Bukkit.broadcastMessage("lmao " + event.getPlayer().getName() + " died");

		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game == null) {
			return;
		}

		this.plugin.statisticsManager.addDeath(player);

		game.removePlayer(player);
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());

		Player killer = player.getKiller();
		Bukkit.broadcastMessage("they were killed by " + (killer == null ? "no one?" : killer.getName()));

		if (killer != null) {
			this.plugin.statisticsManager.addKill(killer);
			game.sendMessage(String.format("§c§l> §f%s §7was killed by §f%s.", player.getName(), killer.getName()));
		} else {
			game.sendMessage(String.format("§c§l> §f%s §7has died!", player.getName()));
		}
	}
}
