package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RangerClass extends GameClass {
	private final ItemStack[] defaultItems;

	public RangerClass(DeepAndDeeper plugin) {
		super(plugin);

		ItemStack[] defaultItems = new ItemStack[41];

		defaultItems[0] = this.plugin.itemManager.item("basic_sword").item();
		defaultItems[1] = this.plugin.itemManager.item("basic_bow").item();
		defaultItems[8] = new ItemStack(Material.ARROW, 32);

		// set the armour to all purple leather
		defaultItems[39] = this.plugin.itemManager.item("basic_helmet").item();
		defaultItems[38] = this.plugin.itemManager.item("basic_chestplate").item();
		defaultItems[37] = this.plugin.itemManager.item("basic_leggings").item();
		defaultItems[36] = this.plugin.itemManager.item("basic_boots").item();

		this.defaultItems = defaultItems;
	}

	@Override
	public void applyEffects(Player player) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
			.setBaseValue(16);

		// add weakness without using a potion effect
		player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
			.setBaseValue(1.1);

		// add slowness without using a potion effect
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			.setBaseValue(0.225);

		// fully restore health and hunger
		player.setHealth(16);
		player.setFoodLevel(20);
	}

	@Override
	public boolean canModifySlot(int slot) {
		return true;
	}

	@Override
	public GameClassType type() {
		return GameClassType.RANGER;
	}

	@Override
	public ItemStack[] defaultItems() {
		return this.defaultItems;
	}
}
