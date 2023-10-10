package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Queue {
	public static final int PARTIES_PER_GAME = 2;

	private final DeepAndDeeper plugin;
	private List<Party> queue = new ArrayList<>();

	public Queue(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public boolean add(Party party) {
		if (this.queue.contains(party)) {
			return false;
		}

		Bukkit.getLogger().info(String.format("Added party %s to queue", party.getLeader().getName()));

		this.queue.add(party);

		if (this.size() >= PARTIES_PER_GAME) {
			List<Party> parties = this.queue;
			this.queue = new ArrayList<>();

			this.plugin.gameManager.startGame(parties);
		}

		return true;
	}

	public boolean contains(Party party) {
		return this.queue.contains(party);
	}

	/**
	 * Removes a party from the queue.
	 *
	 * @param party
	 * @return `true` if the party was in the queue beforehand
	 */
	public boolean remove(Party party) {
		Bukkit.getLogger().info(String.format("Removed party %s from queue", party.getLeader().getName()));

		return this.queue.remove(party);
	}

	public void sendMessage(String message) {
		for (Party party : this.queue) {
			party.sendMessage(message);
		}
	}

	public void sendActionBar(Component message) {
		for (Party party : this.queue) {
			party.sendActionBar(message);
		}
	}

	public int size() {
		return this.queue.size();
	}

	public int maxSize() {
		return PARTIES_PER_GAME;
	}
}
