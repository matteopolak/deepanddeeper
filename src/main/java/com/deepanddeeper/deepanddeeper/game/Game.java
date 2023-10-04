package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game extends BukkitRunnable {
	private DeepAndDeeper plugin;
	private List<Party> parties;
	private World world;
	private Map map;
	private boolean ended = false;

	private Set<Player> livingPlayers = new HashSet<>();

	private int countdown = 5;
	private int id;

	public Game(int id, @NotNull DeepAndDeeper plugin, @NotNull List<Party> parties, @NotNull World world, @NotNull Map map) {
		this.id = id;
		this.plugin = plugin;
		this.parties = parties;
		this.world = world;
		this.map = map;

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
					player.setGameMode(GameMode.ADVENTURE);
					player.teleport(spawn);
					this.plugin.playingTeam.addPlayer(player);
				}
			}
		} else {
			this.sendActionBar(Component.text(""));
			this.cancel();
		}
	}

	public void removePlayer(Player player) throws SQLException {
		this.livingPlayers.remove(player);

		Player reference = null;

		for (Player p : this.livingPlayers) {
			if (reference == null) reference = p;
			else if (this.plugin.partyManager.getParty(p) != this.plugin.partyManager.getParty(reference)) {
				return;
			}
		}

		// At this point, all players are in the same party
		// so the game has ended!
		this.ended = true;

		for (Player p : this.livingPlayers) {
			p.teleport(Bukkit.getWorld("world").getSpawnLocation());
		}

		String worldName = this.world.getName();
		Bukkit.unloadWorld(worldName, false);
		GameManager.freeGameIds.add(this.id);

		if (reference == null) return;

		Party winningParty = this.plugin.partyManager.getParty(reference);

		for (Player p : winningParty.getMembers()) {
			this.plugin.statisticsManager.addWin(p);
			this.plugin.playingTeam.removePlayer(player);
		}

		winningParty.sendMessage("§b§l> §7Your party has won the game!");

		for (Party party : this.parties) {
			if (party == winningParty) continue;

			for (Player p : party.getMembers()) {
				this.plugin.statisticsManager.addLoss(p);
				this.plugin.playingTeam.removePlayer(player);
			}

			party.sendMessage("§b§l> §7Your party has lost the game.");
		}
	}
}
