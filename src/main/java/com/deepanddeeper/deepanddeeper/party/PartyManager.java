package com.deepanddeeper.deepanddeeper.party;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class PartyManager {
    private HashMap<UUID, Party> parties = new HashMap<>();

    public @NotNull Party create(Player leader) {
        Party currentParty = this.getParty(leader);

        if (currentParty != null) {
            if (!currentParty.remove(leader)) {
                for (Player member : currentParty.getMembers()) {
                    parties.remove(member.getUniqueId());
                }
            }
        }

        Party party = new Party(leader);

        parties.put(leader.getUniqueId(), party);

        return party;
    }

    public void join(Player p, Party partyToJoin) {
        if(partyToJoin.add(p)) parties.put(p.getUniqueId(), partyToJoin);
    }

    public void leave(Player p) {
        Party partyToLeave = parties.get(p.getUniqueId());

        if(partyToLeave.remove(p)) parties.remove(p.getUniqueId());
    }

    public @NotNull Party getParty(Player p) {
        return parties.get(p.getUniqueId());
    }
}
