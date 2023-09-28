package com.deepanddeeper.deepanddeeper;

import org.bukkit.command.CommandExecutor;

public interface CommandWithName extends CommandExecutor {
	public String commandName();
}
