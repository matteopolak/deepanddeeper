package com.deepanddeeper.deepanddeeper.commands;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.map.Map;
import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class StartGameCommand implements CommandWithName {


	private DeepAndDeeper plugin;


	public StartGameCommand(DeepAndDeeper plugin) {
		this.plugin = plugin;


	}

	public String commandName() {
		return "startgame";
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (sender instanceof Player player) {

		}

		return true;
	}
}
