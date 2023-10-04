package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.game.Queue;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinQueueAction implements Action {
	private DeepAndDeeper plugin;

	public JoinQueueAction(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@Override
	public UUID id() {
		return UUID.fromString("53d4513c-20c9-48d9-abcc-314305e1a891");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.partyManager.getParty(player);

		player.sendMessage("leader: " + party.getLeader().getName() + " you: " + player.getName(), " equal? " + (party.getLeader() == player));

		if (party.getLeader() != player) {
			player.sendMessage("§c§l> §7Only the party leader can join the queue!");
			return;
		}

		if (!this.plugin.gameManager.queue.add(party)) {
			this.plugin.gameManager.queue.remove(party);

			player.sendMessage("§a§l> §7You have left the queue!");
		} else {
			player.sendMessage("§a§l> §7You have joined the queue!");

			this.plugin.gameManager.queue.sendActionBar(Component.text(String.format(
				"§fWaiting for parties... (§b%d§f/%d)",
				this.plugin.gameManager.queue.size(),
				this.plugin.gameManager.queue.maxSize()
			)));
		}
	}
}
