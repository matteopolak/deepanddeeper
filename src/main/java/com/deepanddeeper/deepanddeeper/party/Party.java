package com.deepanddeeper.deepanddeeper.party;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;

public class Party {

    public static final int MAX_PARTY_SIZE = 3;

    private Player leader;

    public HashSet<Player> members = new HashSet<>();

    private HashSet<Player> invites = new HashSet<>();

    public Party(Player leader) {
        this.leader = leader;
        this.add(leader);
    }

    public boolean add(Player p) {
        if (this.members.size() >= MAX_PARTY_SIZE) {
            p.sendMessage(String.format("§b§l> §7You cannot join §f%s§7's party because it is full.", this.leader.getName()));

            return false;
        }

        if (p != this.getLeader()) {
            p.sendMessage(String.format("§b§l> §7You have joined §f%s§7's party.", this.leader.getName()));
            this.sendMessage(String.format("§b§l> §f%s §7has joined the party.", p.getName()));
        }

        this.members.add(p);
        this.invites.remove(p);


        return true;
    }

    public boolean remove(Player p) {
        if (p == this.leader) {
            if (this.members.size() > 1) {
                Iterator<Player> it = this.members.iterator();

                this.leader = it.next();
                this.members.remove(p);

                p.sendMessage(String.format("§b§l> §7You have left the party; leader has been transferred to §f%s§7.", this.leader.getName()));
                this.sendMessage(String.format("§b§l> §f%s §7has left the party; leader has been transferred to §f%s§7.", p.getName(), this.leader.getName()));

                return true;
            } else {
                return false;
            }
        }

        boolean removed = this.members.remove(p);

        if (removed) {
            p.sendMessage("§b§l> §7You have left the party.");
            this.sendMessage(String.format("§b§l> §f%s §7has left the party.", p.getName()));
        }

        return removed;
    }

    public Iterable<Player> getMembers() {
        return this.members;
    }

    public Player getLeader() {
        return this.leader;
    }

    public void sendMessage(String message) {
        for (Player member : this.members) {
            member.sendMessage(message);
        }
    }

    public void sendActionBar(Component message) {
        for (Player member : this.members) {
            member.sendActionBar(message);
        }
    }

    public void invite(Player player) {
        this.invites.add(player);
    }

    public boolean hasInvite(Player player) {
        return this.invites.contains(player);
    }

    public boolean isFull() {
        return this.members.size() == MAX_PARTY_SIZE;
    }
}
