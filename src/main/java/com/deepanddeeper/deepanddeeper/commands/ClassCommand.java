package com.deepanddeeper.deepanddeeper.commands;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.classes.GameClassType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClassCommand implements CommandWithName {
	private final DeepAndDeeper plugin;

	public ClassCommand(DeepAndDeeper plugin) {
		this.plugin = plugin;
	}

	@Override
	public String commandName() {
		return "class";
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player player) {
			if (args.length != 1) {
				player.sendMessage("§c§l> §7Usage: §f/class <name>§7.");
				return true;
			}

			String className = args[0];
			GameClassType classType = GameClassType.from(className.toLowerCase());
			if (classType == null) {
				player.sendMessage("§c§l> §7You did not provide a valid class name.");

				return true;
			}

			if (this.plugin.gameManager.isInGame(player)) {
				player.sendMessage("§c§l> §7You cannot change your class while in a game.");

				return true;
			}

			boolean successful = this.plugin.classManager.switchClass(player, classType);

			if (successful) {
				player.sendMessage(String.format("§e§l> §7You have switched to the %s §7class.", classType.colouredName()));
			} else {
				player.sendMessage(String.format("§c§l> §7You are already using the %s §7class.", classType.colouredName()));
			}
		}

		return true;
	}
}
