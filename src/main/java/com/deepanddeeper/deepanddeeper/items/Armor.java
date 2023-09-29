package com.deepanddeeper.deepanddeeper.items;

import org.bukkit.Material;

public class Armor extends Item {

    private int armorRating = 1;

    public Armor(String name, Material material, String[] lore) {
        super(name, material, 1, lore);
    }

    public void armorRating(int armorRating) {
        this.armorRating = armorRating;
    }




}
