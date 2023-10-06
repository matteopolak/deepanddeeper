package com.deepanddeeper.deepanddeeper.events;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import com.deepanddeeper.deepanddeeper.actions.Action;
import com.deepanddeeper.deepanddeeper.actions.JoinQueueAction;
import com.deepanddeeper.deepanddeeper.actions.OpenStashAction;
import com.deepanddeeper.deepanddeeper.inventories.InventoryHolderWithId;
import com.deepanddeeper.deepanddeeper.inventories.PlayerStashInventory;
import com.deepanddeeper.deepanddeeper.inventories.WeaponMerchantInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class EntityClickListener implements Listener {
	private HashMap<UUID, InventoryHolder> inventories = new HashMap<>();
	private HashMap<UUID, Action> actions = new HashMap<>();

	public EntityClickListener(DeepAndDeeper plugin) {
		InventoryHolderWithId[] inventories = {
			new WeaponMerchantInventory(plugin),
		};

		Action[] actions = {
			new JoinQueueAction(plugin),
			new OpenStashAction(plugin)
		};

		for (InventoryHolderWithId inventory : inventories) {
			this.inventories.put(inventory.id(), inventory);
		}

		for (Action action : actions) {
			this.actions.put(action.id(), action);
		}
	}

	@EventHandler
	public void onPlayerEntityClick(PlayerInteractEntityEvent event) throws Exception {
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;

		UUID entityId = event.getRightClicked().getUniqueId();

		TextComponent text = Component.text(entityId.toString())
			.clickEvent(ClickEvent.suggestCommand(entityId.toString()))
			.hoverEvent(HoverEvent.showText(Component.text("Click to copy")));

		event.getPlayer().sendMessage(text);

		Action action = this.actions.get(entityId);

		if (action != null) {
			event.setCancelled(true);
			action.perform(event);

			return;
		}

		InventoryHolder inventory = this.inventories.get(entityId);

		if (inventory == null) {
			return;
		}

		event.setCancelled(true);
		event.getPlayer().openInventory(inventory.getInventory());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		var inventory = event.getInventory();

		if (inventory.getHolder(false) instanceof PlayerStashInventory playerInventory) {
			playerInventory.onInventoryClick(event);

			return;
		}

		for (var entry : this.inventories.entrySet()) {
			if (entry.getValue().getInventory().equals(inventory)) {
				event.setCancelled(true);

				var clickedInventory = event.getClickedInventory();

				// If the clicked inventory is the player's, exit immediately
				if (clickedInventory == null || clickedInventory.getType() == InventoryType.PLAYER) {
					return;
				}

				((InventoryHolderWithId) entry.getValue()).onInventoryClick(event);

				return;
			}
		}
	}
}