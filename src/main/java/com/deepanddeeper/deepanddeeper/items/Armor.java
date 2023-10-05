package com.deepanddeeper.deepanddeeper.items;

import org.bukkit.Material;

import java.util.List;

public class Armor extends Item {

    private int armorRating = 1;

    public Armor(String name, Material material, List<String> lore) {
        super("armor", 0, name, material, 1, lore);
    }

    public void armorRating(int armorRating) {
        this.armorRating = armorRating;
    }
}
