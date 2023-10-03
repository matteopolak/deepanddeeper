package com.deepanddeeper.deepanddeeper.party;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class Party {

    private static final int MAX_PARTY_SIZE = 3;

    private Player leader;

    private HashSet<Player> members = new HashSet<>();

    public Party(Player leader) {
        this.leader = leader;
        this.add(leader);
    }

    public boolean add(Player p) {
        if (this.members.size() >= 3)return false;

        this.members.add(p);
        return true;

    }

    public boolean remove(Player p) {
        if (p == this.leader) return false;

        return this.members.remove(p);
    }
}
