package com.deepanddeeper.deepanddeeper.commands.party;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyAcceptCommand implements CommandWithName {

    private DeepAndDeeper plugin;

    public PartyAcceptCommand(DeepAndDeeper plugin) {
        this.plugin = plugin;
    }

    public String commandName() {
        return "accept";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player player) {
            //Make it so you cant accept mid game
            Player partyLeader = Bukkit.getPlayerExact(args[0]);

            if(partyLeader == null) {
                player.sendMessage("§c§l> §7The player whose party you are trying to join does not exist.");
                return false;
            }

            Party partyToJoin = this.plugin.partyManager.getParty(partyLeader);

            if(partyToJoin.hasInvite(player)) {
                partyToJoin.add(player);
                return true;
            } else {
                player.sendMessage("§c§l> §7You have not been invited to this party.");
                return false;
            }
        }

        return false;
    }
}
