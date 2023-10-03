package com.deepanddeeper.deepanddeeper.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyManager {
    private HashMap<UUID, Party> parties = new HashMap<>();

    public void create(Player leader) {
        Party p = new Party(leader);
        parties.put(leader.getUniqueId(), p);
    }

    public void join(Player p, Party partyToJoin) {
        if(partyToJoin.add(p)) parties.put(p.getUniqueId(), partyToJoin);
    }

    public void leave(Player p) {
        Party partyToLeave = parties.get(p.getUniqueId());

        if(partyToLeave.remove(p)) parties.remove(p.getUniqueId());
    }
}
