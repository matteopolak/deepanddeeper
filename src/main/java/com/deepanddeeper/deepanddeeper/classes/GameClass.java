package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.util.InventoryToBase64;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class GameClass {
	protected DeepAndDeeper plugin;

	public GameClass(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	public abstract void applyEffects(Player player);

	/**
	 * Fired when the player activates the class or dies.
	 *
	 * @param player The player who activated the class.
	 */
	public void onActivate(Player player) {
		this.applyEffects(player);

		Connection connection = this.plugin.database.getConnection();

		try (PreparedStatement statement = connection.prepareStatement("""
			INSERT INTO "profile" ("user", "class", "active") VALUES (?, ?, TRUE)
				ON CONFLICT ("user", "class") DO UPDATE SET "active" = TRUE;
		""")) {
			statement.setObject(1, player.getUniqueId());
			statement.setInt(2, this.type().id());

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!this.readInventory(player, this.type())) {
			player.getInventory().setContents(this.defaultItems());
		}
	}

	/**
	 * Fired when the player deactivates the class. Unlike `onActivate`, this is
	 * not fired when the player dies.
	 *
	 * @param player The player who deactivated the class.
	 */
	public void onDeactivate(Player player) {
		Connection connection = this.plugin.database.getConnection();

		try (
			PreparedStatement statement = connection.prepareStatement("""
				UPDATE "profile" SET "active" = FALSE WHERE "user" = ? AND "class" = ?;
			""");
		) {
			statement.setObject(1, player.getUniqueId());
			statement.setInt(2, this.type().id());

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
			.setBaseValue(20);

		this.saveInventory(player, this.type());
		player.getInventory().clear();

		// remove weakness without using a potion effect
		player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
			.setBaseValue(1);

		// remove slowness without using a potion effect
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			.setBaseValue(0.2);
	}

	/**
	 * Fired when the player clicks in their inventory.
	 *
	 * @param slot The slot that was clicked
	 * @return `false` if the event should be canceled
	 */
	public abstract boolean canModifySlot(int slot);

	public abstract GameClassType type();
	public abstract ItemStack[] defaultItems();

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
