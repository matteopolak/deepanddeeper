package com.deepanddeeper.deepanddeeper.items;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Material;

import java.util.List;

public class Armor extends Item {

    private int armorRating = 1;

    public Armor(DeepAndDeeper plugin, String name, Material material, List<String> lore) {
        super(plugin, "armor", 0, name, material, 1, lore);
    }

    public void armorRating(int armorRating) {
        this.armorRating = armorRating;
    }
}
