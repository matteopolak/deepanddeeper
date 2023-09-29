package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerJoinListener implements Listener {
	private DeepAndDeeper plugin;
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	private Team playingTeam;

	public PlayerJoinListener(DeepAndDeeper plugin) {
		this.plugin = plugin;

		try {
			this.playingTeam = this.scoreboard.registerNewTeam("playing");
			this.playingTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		} catch (IllegalArgumentException e) {
			// The team already exists
			this.playingTeam = this.scoreboard.getTeam("playing");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Teleport the player to the lobby
		player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5, 0, 0));

		// Remove the player from the "playing" team when they join
		boolean wasInGame = this.playingTeam.removePlayer(player);

		if (wasInGame) {
			// send message to player using colour codes, e.g. "&cYou left the game &cuh no!"
			player.sendMessage(Component.text("You left the game", NamedTextColor.RED));

			// now using colour escape codes
			player.sendMessage("§cYou left the game §6uh no!");
		}

		this.playingTeam.addPlayer(player);
	}
}