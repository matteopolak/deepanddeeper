package com.deepanddeeper.deepanddeeper.itemmanager;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public class ItemManager implements CommandExecutor {

    ItemStack sword;

    public ItemManager() {

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);

        TextComponent name = (TextComponent) item.getItemMeta().displayName();
        if(name != null) {
            item.getItemMeta().displayName(name.content("Test Sword"));
        }

        sword = item;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("givesword")) {
            player.getInventory().addItem(sword);
        }

        return true;
    }
}