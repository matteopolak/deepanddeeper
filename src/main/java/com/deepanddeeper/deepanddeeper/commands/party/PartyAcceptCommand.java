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
		if (sender instanceof Player player) {
			if (args.length == 0) {
				player.sendMessage("§c§l> §7Please specify the name of the player you would like to join.");
				return true;
			}

			Player partyLeader = Bukkit.getPlayerExact(args[0]);

			if (partyLeader == null) {
				player.sendMessage("§c§l> §7The player whose party you are trying to join does not exist.");
				return true;
			}

			Game currentGame = this.plugin.gameManager.games.get(player.getUniqueId());
			if (currentGame != null && !currentGame.hasEnded()) {
				player.sendMessage("§c§l> §7You cannot accept an invite during a game!");
				return true;
			}

			Game inviteGame = this.plugin.gameManager.games.get(partyLeader.getUniqueId());
			if (inviteGame != null && !inviteGame.hasEnded()) {
				player.sendMessage("§c§l> §7You cannot accept an invite from a party that is currently in a game!");
				return true;
			}

			if (!this.plugin.classManager.classes.containsKey(player.getUniqueId())) {
				player.sendMessage("§c§l> §7You must select a class before joining a party.");
				return true;
			}

			Party partyToJoin = this.plugin.partyManager.getParty(partyLeader);

			if (partyToJoin.hasInvite(player)) {
				this.plugin.partyManager.join(player, partyToJoin);
				return true;
			} else {
				player.sendMessage("§c§l> §7You have not been invited to this party.");
				return true;
			}
		}

		return true;
	}
}
