package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {
	private static int totalGames = 0;
	public static List<Integer> freeGameIds = new ArrayList<>();

	public Queue queue;
	public java.util.Map<UUID, Game> games = new HashMap<>();

	private DeepAndDeeper plugin;
	private Map[] maps;
	private Random random = new Random();

	public GameManager(DeepAndDeeper plugin) {
		this.plugin = plugin;
		this.queue = new Queue(plugin);

		this.maps = plugin.getConfig().getList("maps").stream()
			.map(map -> Map.deserialize((java.util.Map<String, Object>) map))
			.toArray(Map[]::new);
	}

	public boolean isInGame(Player player) {
		Game game = this.games.get(player.getUniqueId());

		return game != null && !game.hasEnded();
	}

	public Game startGame(List<Party> parties) {
		int nextId = GameManager.freeGameIds.isEmpty() ? GameManager.totalGames++ : GameManager.freeGameIds.remove(0);

		String worldName = String.format("game-%d", nextId);
		Map map = this.chooseRandomMap();
		World world = map.generate(worldName);

		Game game = new Game(nextId, this.plugin, parties, world, map);

		for (Party party : parties) {
			for (Player player : party.getMembers()) {
				this.games.put(player.getUniqueId(), game);
			}
		}

		game.countdownAndStart();

		return game;
	}

	private Map chooseRandomMap() {
		return this.maps[this.random.nextInt(this.maps.length)];
	}
}
