package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum GameState {
	STARTING,
	STARTED,
}

public class Game extends BukkitRunnable {
	private DeepAndDeeper plugin;
	private List<Party> parties;
	private World world;
	private Map map;
	private boolean ended = true;
	private GameState state;
	private int borderIndex = 0;
	private long borderTimeLeft = 0;

	private Set<Player> livingPlayers = new HashSet<>();

	private int countdown = 5;
	private int id;

	public Game(int id, @NotNull DeepAndDeeper plugin, @NotNull List<Party> parties, @NotNull World world, @NotNull Map map) {
		this.id = id;
		this.plugin = plugin;
		this.parties = parties;
		this.world = world;
		this.map = map;
		this.state = GameState.STARTING;

		for (Party party : this.parties) {
			for (Player player : party.getMembers()) {
				this.livingPlayers.add(player);
			}
		}
	}

	public @NotNull List<Party> getParties() {
		return this.parties;
	}

	public @NotNull World getWorld() {
		return this.world;
	}

	public boolean hasEnded() {
		return this.ended;
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

	private void clearDroppedItems() {
		for (Entity entity : this.world.getEntities()) {
			if (entity instanceof Item item) {
				item.remove();
			}
		}
	}

	@Override
	public void run() {
		if (this.state == GameState.STARTING) {
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

				this.clearDroppedItems();

				var borders = this.map.borders();
				var firstBorder = borders.get(0);

				if (firstBorder != null) {
					this.world.getWorldBorder().setSize(firstBorder.first);

					var secondBorder = borders.get(1);

					if (secondBorder != null) {
						WorldBorder border = this.world.getWorldBorder();

						border.setDamageBuffer(0);
						border.setWarningDistance(2);
						border.setWarningTime(5);
						border.setCenter(0.5, 0.5);
						border.setSize(secondBorder.first, secondBorder.second);

						this.borderIndex = 2;
						this.borderTimeLeft = secondBorder.second;
					} else {
						this.cancel();
					}
				} else {
					this.cancel();
				}

				this.ended = false;

				for (int i = 0; i < this.parties.size(); ++i) {
					Location spawn = this.map.spawns().get(i).location(this.world);

					for (Player player : this.parties.get(i).getMembers()) {
						player.setGameMode(GameMode.ADVENTURE);
						player.setFoodLevel(5);
						player.teleport(spawn);

						this.plugin.playingTeam.addPlayer(player);
					}
				}
			} else {
				this.sendActionBar(Component.text(""));
				this.state = GameState.STARTED;
			}
		} else {
			this.borderTimeLeft--;

			if (this.borderTimeLeft > 0) return;

			var borders = this.map.borders();
			var border = borders.get(this.borderIndex);

			if (border != null) {
				this.world.getWorldBorder().setSize(border.first, border.second);
				this.borderIndex++;

				if (this.borderIndex < borders.size()) {
					this.borderTimeLeft = border.second;
				} else {
					this.cancel();
				}
			} else {
				this.cancel();
			}
		}
	}

	public void end() {
		this.ended = true;

		String worldName = this.world.getName();
		Bukkit.unloadWorld(worldName, false);

		GameManager.freeGameIds.add(this.id);
	}

	public void removePlayer(Player player) {
		this.livingPlayers.remove(player);
		this.plugin.playingTeam.removePlayer(player);

		if (this.livingPlayers.isEmpty()) {
			this.end();
		}
	}
}
