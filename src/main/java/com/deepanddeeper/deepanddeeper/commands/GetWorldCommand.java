package com.deepanddeeper.deepanddeeper.commands;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetWorldCommand implements CommandWithName {
	public String commandName() {
		return "world";
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (sender instanceof Player player) {
			String worldName = player.getWorld().getName();

			player.sendMessage(Component.text("You are currently in " + worldName));
		}

		return true;
	}
}
