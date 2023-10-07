package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.util.InventoryToBase64;
import org.bukkit.entity.Player;

import java.io.IOException;
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

	/**
	 * Fired when the player clicks in their inventory.
	 *
	 * @param slot The slot that was clicked
	 * @return `false` if the event should be canceled
	 */
	public abstract boolean canModifySlot(int slot);

	public abstract GameClassType type();

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

	// Reads the specified inventory then deletes it from disk
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

			// delete file
			Files.delete(path);

			// decode string to inventory
			player.getInventory().setContents(InventoryToBase64.fromBase64(encoded));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
