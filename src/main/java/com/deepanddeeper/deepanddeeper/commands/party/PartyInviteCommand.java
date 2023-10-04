package com.deepanddeeper.deepanddeeper.commands.party;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyInviteCommand implements CommandWithName {
    public String commandName() {
        return "invite";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(sender instanceof Player player) {

            //Make sure that a player cannot be invited when the party is full
            //Make sure you are the party leader
            for(String s : args) {
                Player playerToInvite = Bukkit.getPlayerExact(s);

                if(playerToInvite != null) {
                    playerToInvite.sendMessage(String.format("§b§l> §7 You are being invited by %s, type /accept to join their party!", player.getName()));
                    //add message to party when the player gets invited
                } else {
                    player.sendMessage("§b§l> §7 This player does not exist.");
                }

            }

        }

        return true;
    }
}
