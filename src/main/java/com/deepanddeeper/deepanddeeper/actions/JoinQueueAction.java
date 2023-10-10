package com.deepanddeeper.deepanddeeper.actions;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class JoinQueueAction implements Action {
	private final DeepAndDeeper plugin;

	public JoinQueueAction(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@Override
	public UUID id() {
		return UUID.fromString("366d8a2f-2c1e-47e6-9a57-ff872f2a1e19");
	}

	@Override
	public void perform(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Party party = this.plugin.partyManager.getParty(player);

		if (party.getLeader() != player) {
			player.sendMessage("§c§l> §7Only the party leader can join the queue!");
			return;
		}

		if (this.plugin.gameManager.isInGame(player)) {
			player.sendMessage("§c§l> §7You cannot join the queue while in a game!");
			return;
		}

		if (!this.plugin.classManager.classes.containsKey(player.getUniqueId())) {
			player.sendMessage("§c§l> §7You must select a class before joining the queue.");
			return;
		}

		if (this.plugin.gameManager.queue.contains(party)) {
			player.sendMessage("§a§l> §7You have left the queue!");

			this.plugin.gameManager.queue.remove(party);
			this.plugin.gameManager.queue.sendActionBar(Component.text(String.format(
				"§fWaiting for parties... (§b%d§f/%d)",
				this.plugin.gameManager.queue.size(),
				this.plugin.gameManager.queue.maxSize()
			)));
		} else {
			player.sendMessage("§a§l> §7You have joined the queue!");

			this.plugin.gameManager.queue.add(party);
			this.plugin.gameManager.queue.sendActionBar(Component.text(String.format(
				"§fWaiting for parties... (§b%d§f/%d)",
				this.plugin.gameManager.queue.size(),
				this.plugin.gameManager.queue.maxSize()
			)));
		}
	}
}
