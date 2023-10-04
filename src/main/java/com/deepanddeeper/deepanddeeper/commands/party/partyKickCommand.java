package com.deepanddeeper.deepanddeeper.commands.party;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class partyKickCommand implements CommandWithName {
    public String commandName() {
        return "kick";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return true;
    }
}
