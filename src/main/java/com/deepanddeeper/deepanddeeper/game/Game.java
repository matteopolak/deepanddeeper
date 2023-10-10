package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import com.deepanddeeper.deepanddeeper.util.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

enum GameState {
	STARTING,
	STARTED,
}

public class Game extends BukkitRunnable {
	private final DeepAndDeeper plugin;
	private final List<Party> parties;
	private World world;
	private final Map map;
	private boolean started = false;
	private boolean ended = false;
	private GameState state;
	private int borderIndex = 0;
	private long borderTimeLeft = 0;

	private final Set<Player> livingPlayers = new HashSet<>();

	private int countdown = 5;
	private final int id;

	private final Random random = new Random();

	public Game(int id, @NotNull DeepAndDeeper plugin, @NotNull List<Party> parties, @Nullable World world, @NotNull Map map) {
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

	public @NotNull World world() {
		return this.world;
	}
	public void world(World world) {
		this.world = world;
	}

	public boolean ended() {
		return this.ended;
	}
	public boolean started() {
		return this.started;
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

				this.started = true;

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

				this.spawnPortals(border);

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

	public void spawnPortals(Pair<Double, Long> border) {
		int sideLength = border.first.intValue() / 2;
		int upperBound = (int) (border.first * Math.sqrt(border.first) * 0.1);

		for (int x = -sideLength; x < border.first; x += 5) {
			for (int z = -sideLength; z < border.first; z += 5) {
				for (int y = -16; y < 16; ++y) {
					Location location = new Location(this.world, x, y, z);

					if (location.getBlock().isPassable()) continue;
					if (!location.add(0, 1, 0).getBlock().isPassable()) continue;
					if (!location.add(0, 1, 0).getBlock().isPassable()) continue;

					location.add(0, -1, 0);

					boolean hasAirAround = true;
					boolean hasBlocksUnder = true;

					for (int x2 = -1; x2 <= 1; ++x2) {
						for (int z2 = -1; z2 <= 1; ++z2) {
							if (!location.clone().add(x2, 0, z2).getBlock().isPassable()) {
								hasAirAround = false;

								if (!hasBlocksUnder) break;
							}

							if (location.clone().add(x2, -1, z2).getBlock().isPassable()) {
								hasBlocksUnder = false;

								if (!hasAirAround) break;
							}
						}
					}

					if (!hasAirAround || !hasBlocksUnder) continue;

					boolean hasBlockAbove = false;

					// ensure there is a block above the block at most 16 block above
					for (int y2 = 1; y2 <= 16; ++y2) {
						if (!location.clone().add(0, y2, 0).getBlock().isPassable()) {
							hasBlockAbove = true;
							break;
						}
					}

					if (!hasBlockAbove) continue;
					if (this.random.nextInt(upperBound) != 0) continue;

					// set the block to a prismarine wall
					location.getBlock().setType(Material.PRISMARINE_WALL);

					this.sendMessage("§9§l> §7An escape portal has opened!");
				}
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
