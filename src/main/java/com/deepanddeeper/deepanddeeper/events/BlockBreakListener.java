package com.deepanddeeper.deepanddeeper.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockBreakListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		event.getPlayer().sendMessage("you moved");
	}
}