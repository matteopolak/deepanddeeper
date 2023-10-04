package com.deepanddeeper.deepanddeeper.game;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.deepanddeeper.deepanddeeper.party.Party;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class GameManager {
	private static int totalGames = 0;

	public Queue queue;

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

	public Game startGame(List<Party> parties) {
		String worldName = String.format("game-%d", GameManager.totalGames++);
		Map map = this.chooseRandomMap();
		World world = map.generate(worldName);

		Game game = new Game(this.plugin, parties, world, map);

		game.countdownAndStart();

		return game;
	}

	private Map chooseRandomMap() {
		return this.maps[this.random.nextInt(this.maps.length)];
	}
}
