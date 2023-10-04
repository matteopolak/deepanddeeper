package com.deepanddeeper.deepanddeeper.map;

import com.deepanddeeper.deepanddeeper.generators.VoidGenerator;
import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Map {
	private File file;
	private ClipboardFormat clipboard;

	private List<Spawn> spawns;
	private String id;
	private String name;


	public Map(String fileName) {
		this.file = new File(Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit").getDataFolder(), "schematics/" + fileName);
		this.clipboard = ClipboardFormats.findByFile(this.file);

		Bukkit.getLogger().info("Successfully loaded map " + this.file.exists());
	}

	public Map spawns(List<Spawn> spawns) {
		this.spawns = spawns;
		return this;
	}

	public List<Spawn> spawns() {
		return this.spawns;
	}

	public Map id(String id) {
		this.id = id;
		return this;
	}

	public Map name(String name) {
		this.name = name;
		return this;
	}

	public static @NotNull Map deserialize(java.util.Map<String, Object> data) {
		String id = (String) data.get("id");
		String name = (String) data.get("name");
		String schematic = (String) data.get("schematic");

		List<Spawn> spawns = ((List<java.util.Map<String, Object>>) data.get("spawns")).stream()
			.map(Spawn::deserialize)
			.toList();

		return new Map(schematic)
			.id(id)
			.name(name)
			.spawns(spawns);
	}

	private void deleteWorld(String worldName) throws IOException {
		Bukkit.unloadWorld(worldName, false);

		FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + worldName));
	}

	public World generate(String worldName) {
		// delete world if it exists
		World oldWorld = Bukkit.getWorld(worldName);

		if (oldWorld != null) {
			try {
				this.deleteWorld(worldName);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		WorldCreator worldCreator = new WorldCreator(worldName);
		worldCreator.generator(new VoidGenerator());

		World world = Bukkit.createWorld(worldCreator);

		world.setAutoSave(false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.FALL_DAMAGE, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

		try {
			ClipboardReader reader = this.clipboard.getReader(new FileInputStream(this.file));
			EditSession session = WorldEdit.getInstance()
				.newEditSessionBuilder()
				.world(FaweAPI.getWorld(worldName))
				.build();

			Operation operation = new ClipboardHolder(reader.read())
				.createPaste(session)
				.to(BlockVector3.at(0, 0, 0))
				// Needed to overwrite previous maps
				.ignoreAirBlocks(false)
				.build();

			Operations.complete(operation);
			session.close();

			Bukkit.getLogger().info("Successfully generated world " + worldName);
		} catch (IOException e) {
			e.printStackTrace();

			throw new RuntimeException(e);
		}

		return world;
	}
}
