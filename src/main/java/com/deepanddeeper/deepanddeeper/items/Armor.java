package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Armor extends Item {
    private int armorRating = 1;

    public Armor(DeepAndDeeper plugin, String id, int price, String name, Material material, List<String> lore) {
        super(plugin, id, price, name, material, 1, lore);
    }

    public void armorRating(int armorRating) {
        this.armorRating = armorRating;
    }

    public static @NotNull Armor deserialize(DeepAndDeeper plugin, Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        List<String> lore = (List<String>) data.get("lore");
        Material material = Material.valueOf((String) data.get("material"));
        int price = (int) data.get("price");

        return new Armor(plugin, id, price, name, material, lore);
    }
}
