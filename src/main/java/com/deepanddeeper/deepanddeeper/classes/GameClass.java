package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.util.InventoryToBase64;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class GameClass {
	protected DeepAndDeeper plugin;

	public GameClass(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	/**
	 * Fired when the player activates the class or dies.
	 *
	 * @param player The player who activated the class.
	 */
	public abstract void onActivate(Player player);

	/**
	 * Fired when the player deactivates the class. Unlike `onActivate`, this is
	 * not fired when the player dies.
	 *
	 * @param player The player who deactivated the class.
	 */
	public abstract void onDeactivate(Player player);

	protected boolean saveInventory(Player player, GameClassType type) {
		String filename = type.filename();

		Path path = this.plugin.getDataFolder()
			.toPath()
			.resolve("players")
			.resolve(player.getUniqueId().toString())
			.resolve(filename);

		// save player inventory to path
		String encoded = InventoryToBase64.toBase64(player.getInventory());

		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, encoded, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	protected boolean readInventory(Player player, GameClassType type) {
		String filename = type.filename();

		Path path = this.plugin.getDataFolder()
			.toPath()
			.resolve("players")
			.resolve(player.getUniqueId().toString())
			.resolve(filename);

		try {
			// read encoded string from file
			String encoded = Files.readString(path);

			// decode string to inventory
			player.getInventory().setContents(InventoryToBase64.fromBase64(encoded));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
