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

public class PartyKickCommand implements CommandWithName {

    private DeepAndDeeper plugin;

    public PartyKickCommand(DeepAndDeeper plugin) {
        this.plugin = plugin;
    }

    public String commandName() {
        return "kick";
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
                player.sendMessage("§c§l> §7You cannot kick a player during a game!");
                return false;
            }

            Player playerToKick = Bukkit.getPlayerExact(args[0]);

            if(party.members.contains(playerToKick)) {
                party.remove(playerToKick);
            } else {
                player.sendMessage("§b§l> §fPlayer could not be found.");
            }

        }
        return true;

    }
}
