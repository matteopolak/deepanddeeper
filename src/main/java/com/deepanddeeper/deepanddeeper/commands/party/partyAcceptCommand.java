package com.deepanddeeper.deepanddeeper.commands.party;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class partyAcceptCommand implements CommandWithName {
    public String commandName() {
        return "accept";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return true;
    }
}
