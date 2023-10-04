package com.deepanddeeper.deepanddeeper.commands.party;

import com.deepanddeeper.deepanddeeper.CommandWithName;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.game.Game;
import com.deepanddeeper.deepanddeeper.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyInviteCommand implements CommandWithName {
    private DeepAndDeeper plugin;

    public PartyInviteCommand(DeepAndDeeper plugin) {
        this.plugin = plugin;
    }

    public String commandName() {
        return "invite";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(sender instanceof Player player) {

            Party party = this.plugin.partyManager.getParty(player);

            if(party.getLeader() != player) {
                player.sendMessage("§c§l> §7You cannot invite a player unless you are the party leader!");
                return false;
            }

            Game game = this.plugin.gameManager.games.get(player.getUniqueId());
            if(game != null && !game.hasEnded()) {
                player.sendMessage("§c§l> §7You cannot invite a player during a game!");
                return false;
            }

            if(party.isFull()) {
                player.sendMessage("§c§l> §7Your party is full.");
                return false;
            }

            for(String s : args) {
                Player playerToInvite = Bukkit.getPlayerExact(s);

                if(playerToInvite != null) {
                    playerToInvite.sendMessage(String.format("§b§l> §7You are being invited by §f%s§7, type §f/accept§7 to join their party!", player.getName()));
                    party.sendMessage(String.format("§b§l> §f%s §7has been invited to your party!", playerToInvite.getName()));
                    party.invite(playerToInvite);
                } else {
                    player.sendMessage(String.format("§b§l> §f%s §7could not be found.", s));
                }

            }

        }

        return true;
    }
}
