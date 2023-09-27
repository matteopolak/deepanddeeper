package com.deepanddeeper.deepanddeeper.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockBreakListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		event.getPlayer().sendMessage(Component.text("hello world", TextColor.color(255, 0, 0)));
	}
}