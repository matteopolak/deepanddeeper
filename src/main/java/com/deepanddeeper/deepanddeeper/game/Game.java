package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Game extends BukkitRunnable {
	private DeepAndDeeper plugin;
	private List<Party> parties;
	private World world;
	private Map map;

	private int countdown = 5;

	public Game(@NotNull DeepAndDeeper plugin, @NotNull List<Party> parties, @NotNull World world, @NotNull Map map) {
		this.plugin = plugin;
		this.parties = parties;
		this.world = world;
		this.map = map;
	}

	public @NotNull List<Party> getParties() {
		return this.parties;
	}

	public @NotNull World getWorld() {
		return this.world;
	}

	public void sendMessage(String message) {
		for (Party party : this.parties) {
			party.sendMessage(message);
		}
	}

	public void sendActionBar(Component message) {
		for (Party party : this.parties) {
			party.sendActionBar(message);
		}
	}

	public void countdownAndStart() {
		this.runTaskTimer(this.plugin, 20, 20);
	}

	@Override
	public void run() {
		if (this.countdown > 0) {
			this.sendActionBar(Component.text(String.format(
				"§fGame starting in §b%d second%s§f...",
				this.countdown,
				this.countdown == 1 ? "" : "s"
			)));
			this.countdown--;
		} else if (this.countdown == 0) {
			this.sendActionBar(Component.text("§fGame starting..."));
			this.countdown--;

			for (int i = 0; i < this.parties.size(); ++i) {
				Location spawn = this.map.spawns().get(i).location(this.world);

				for (Player player : this.parties.get(i).getMembers()) {
					player.teleport(spawn);
				}
			}
		} else {
			this.sendActionBar(Component.text(""));
			this.cancel();
		}
	}
}
