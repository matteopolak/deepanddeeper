package com.deepanddeeper.deepanddeeper.items;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.items.weapons.Fireball;
import com.deepanddeeper.deepanddeeper.items.weapons.Haste;
import com.deepanddeeper.deepanddeeper.items.weapons.Invisibility;
import com.deepanddeeper.deepanddeeper.items.weapons.Lightning;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Weapon extends Item {
    private double damage = 1;
    private long cooldown = 1_200;

    public Weapon(DeepAndDeeper plugin, String id, int price, String name, Material material, List<String> lore) {
        super(plugin, id, price, name, material, 1, lore);
    }

    public Weapon damage(double damage) {
        this.damage = damage;
        return this;
    }

    public Weapon cooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public long cooldown() {
        return this.cooldown;
    }

    public void onHit() {
        //Do something
    }

    public boolean canActivate() {
        return false;
    }

    public boolean onActivate(PlayerInteractEvent event) {
        long cooldown = this.plugin.itemManager.remainingCooldown(event.getPlayer(), this);

        if (cooldown > 0) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format("§c§l> §f%s §7is on cooldown for §f%,.1f seconds§7.", this.name().content(), ((double) cooldown) / 1_000));

            return false;
        }

        return true;
    }

    public static @NotNull Weapon deserialize(DeepAndDeeper plugin, Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        List<String> lore = (List<String>) data.get("lore");
        double damage = (double) data.get("damage");
        double cooldown = (double) data.get("cooldown");
        Material material = Material.valueOf((String) data.get("material"));
        int price = (int) data.get("price");

        return (switch (id) {
            case "wizard_fireball" -> new Fireball(plugin, id, price, name, material, lore);
            case "wizard_lightning" -> new Lightning(plugin, id, price, name, material, lore);
            case "wizard_haste" -> new Haste(plugin, id, price, name, material, lore);
            case "wizard_invisibility" -> new Invisibility(plugin, id, price, name, material, lore);
            default -> new Weapon(plugin, id, price, name, material, lore);
        })
            .damage(damage)
            .cooldown((long) (cooldown * 1000d));
    }
}
