package com.deepanddeeper.deepanddeeper.items;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public class Item {
    ItemStack item;
    protected TextComponent name = Component.text("Name");
    private Material material = Material.DIAMOND_SWORD;
    private List<TextComponent> lore;

    private int amount = 1;

    public Item() {
        initializeItem();
    }

    public Item(String name, Material material, int amount, String[] lore) {
        this.name = Component.text("name");
        this.material = material;
        this.amount = amount;


        this.lore = Arrays.stream(lore)
                .map(Component::text)
                .toList();

        initializeItem();
    }

    private void initializeItem() {
        this.item = new ItemStack(this.material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(this.name);
        meta.lore(this.lore);

        this.item.setItemMeta(meta);
    }

}
