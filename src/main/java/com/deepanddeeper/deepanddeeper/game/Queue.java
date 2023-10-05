package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class Queue {
	public static final int PARTIES_PER_GAME = 1;

	private DeepAndDeeper plugin;
	private List<Party> queue = new ArrayList<>();

	public Queue(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public boolean add(Party party) {
		if (this.queue.contains(party)) {
			return false;
		}

		this.queue.add(party);

		if (this.size() >= PARTIES_PER_GAME) {
			this.plugin.gameManager.startGame(this.queue);
			this.queue = new ArrayList<>();
		}

		return true;
	}

	/**
	 * Removes a party from the queue.
	 * @param party
	 * @return `true` if the party was in the queue beforehand
	 */
	public boolean remove(Party party) {
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
