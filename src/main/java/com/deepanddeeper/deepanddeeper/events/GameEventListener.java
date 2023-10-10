package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClass;
import com.deepanddeeper.deepanddeeper.game.Game;
import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HoldData {
	public long firstHeld;
	public long lastHeld;

	public HoldData(long held) {
		this.firstHeld = held;
		this.lastHeld = held;
	}
}

public class GameEventListener implements Listener {
	private final DeepAndDeeper plugin;
	private final Map<UUID, HoldData> pendingEscapePortalOpenTasks = new HashMap<>();

	public GameEventListener(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	// on player death
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game == null || game.hasEnded()) {
			return;
		}

		this.plugin.statisticsManager.addDeath(player);

		game.removePlayer(player);
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());

		Player killer = player.getKiller();

		if (killer != null) {
			this.plugin.statisticsManager.addKill(killer);
			game.sendMessage(String.format("§c§l> §f%s §7was killed by §f%s§7.", player.getName(), killer.getName()));
		} else {
			game.sendMessage(String.format("§c§l> §f%s §7has died!", player.getName()));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game != null && !game.hasEnded() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block == null) return;

			PotionEffect effect = switch (block.getType()) {
				case LIME_WOOL -> new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 0);
				case LIGHT_BLUE_WOOL -> new PotionEffect(PotionEffectType.SPEED, 20 * 10, 0);
				case RED_WOOL -> new PotionEffect(PotionEffectType.REGENERATION, 1, 9);
				case ORANGE_WOOL -> new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0);
				default -> null;
			};

			if (effect != null) {
				player.addPotionEffect(effect);

				Material material = block.getType();

				// spawn a bunch of particles with the same colour as the wool on the block
				ParticleBuilder particleBuilder = new ParticleBuilder(Particle.REDSTONE)
					.location(block.getLocation().add(0.5, 0.5, 0.5))
					.count(100)
					.offset(0.5, 0.5, 0.5)
					.extra(2)
					.allPlayers();

				// use the RGB values of the wool to set the particle colour
				switch (material) {
					case LIME_WOOL -> particleBuilder.color(57, 186, 46);
					case LIGHT_BLUE_WOOL -> particleBuilder.color(99, 135, 210);
					case RED_WOOL -> particleBuilder.color(158, 43, 39);
					case ORANGE_WOOL -> particleBuilder.color(234, 126, 53);
				}

				particleBuilder.spawn();

				// set the block to gray wool, then back to the original material
				// after 30 seconds
				block.setType(Material.GRAY_WOOL);

				Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
					block.setType(material);
				}, 20 * 30);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());
			Inventory inventory = event.getClickedInventory();

			// disable modifying the hotbar when a class is equipped
			if (gameClass != null && inventory != null && inventory.getType() == InventoryType.PLAYER && !gameClass.canModifySlot(event.getSlot())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		GameClass gameClass = this.plugin.classManager.classes.get(player.getUniqueId());
		int slot = player.getInventory().getHeldItemSlot();

		if (gameClass != null && !gameClass.canModifySlot(slot)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game != null) {
			player.setHealth(0);

			game.sendMessage(String.format("§c§l> §f%s §7has left the game!", player.getName()));
			game.removePlayer(player);
		}
	}

	// check if player is holding right click on a prismarine wall
	// if they hold right click for 5 seconds, teleport them to the lobby
	@EventHandler
	public void onPlayerInteractWithEscape(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		Game game = this.plugin.gameManager.games.get(player.getUniqueId());

		if (game != null && !game.hasEnded() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block == null) return;

			if (block.getType() == Material.PRISMARINE_WALL) {
				long now = System.currentTimeMillis();

				HoldData data = this.pendingEscapePortalOpenTasks
					.computeIfAbsent(player.getUniqueId(), uuid -> new HoldData(now));

				// if their last hold was within the last 100ms, increment the ticks held
				if (now - data.lastHeld > 300) {
					data.firstHeld = now;
				}

				data.lastHeld = now;

				// One iteration is around 200ms
				if (now - data.firstHeld >= 5_000) {
					// replace the block with a blue banner
					block.setType(Material.BLUE_BANNER);

					player.sendActionBar(Component.text("§fYou have opened a §9blue portal§f!"));
				} else {
					// send a message to the player with how long they have left
					player.sendActionBar(Component.text(String.format(
						"§fOpening portal... (§b%d seconds left§f)",
						(int) Math.ceil(((double) (5_000 - (now - data.firstHeld))) / (double) 1_000))
					));
				}
			}
		}
	}

	// if the player walks into a blue banner, teleport them to the lobby
	@EventHandler
	public void onPlayerWalkIntoEscapePortal(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Block block = player.getLocation().getBlock();

		if (block.getType() == Material.BLUE_BANNER) {
			Game game = this.plugin.gameManager.games.get(player.getUniqueId());

			if (game != null && !game.hasEnded()) {
				player.teleport(Bukkit.getWorld("world").getSpawnLocation());
				player.sendMessage("§b§l> §7You have escaped the dungeon!");
				game.removePlayer(player);
			}
		}
	}
}
