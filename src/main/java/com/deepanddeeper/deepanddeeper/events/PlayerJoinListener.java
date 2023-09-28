package com.deepanddeeper.deepanddeeper.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Teleport the player to the lobby
		player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5, 0, 0));
	}
}