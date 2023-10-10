package com.deepanddeeper.deepanddeeper.actions;

import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public interface Action {
	// The UUID of the entity that should activate the action
	UUID id();

	// The action to perform
	void perform(PlayerInteractEntityEvent event) throws Exception;
}
