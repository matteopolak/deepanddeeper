package com.deepanddeeper.deepanddeeper.items;
import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
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

    public static @NotNull Weapon deserialize(DeepAndDeeper plugin, Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        List<String> lore = (List<String>) data.get("lore");
        double damage = (double) data.get("damage");
        double cooldown = (double) data.get("cooldown");
        Material material = Material.valueOf((String) data.get("material"));
        int price = (int) data.get("price");

        return new Weapon(plugin, id, price, name, material, lore)
          .damage(damage)
          .cooldown((long) (cooldown * 1000d));
    }
}
