package com.deepanddeeper.deepanddeeper.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	private World lobbyWorld;

	public PlayerJoinListener(World lobbyWorld) {
		this.lobbyWorld = lobbyWorld;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Teleport the player to the lobby
		player.teleport(new Location(this.lobbyWorld, 0, 0, 0, 0, 0));
	}
}