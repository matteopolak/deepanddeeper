package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Armor extends Item {
    private int armorRating = 1;

    public Armor(DeepAndDeeper plugin, String id, int buyPrice, int sellPrice, String name, Material material, List<String> lore) {
        super(plugin, id, buyPrice, sellPrice, name, material, 1, lore);
    }

    public void armorRating(int armorRating) {
        this.armorRating = armorRating;
    }

    public static @NotNull Armor deserialize(DeepAndDeeper plugin, Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        List<String> lore = (List<String>) data.get("lore");
        Material material = Material.valueOf((String) data.get("material"));
        int buyPrice = (int) data.get("buy_price");
        int sellPrice = (int) data.get("sell_price");

        return new Armor(plugin, id, buyPrice, sellPrice, name, material, lore);
    }
}
