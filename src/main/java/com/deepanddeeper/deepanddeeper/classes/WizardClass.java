package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WizardClass extends GameClass {
	private final ItemStack[] defaultItems;
	private WizardSpell spell;

	public WizardClass(DeepAndDeeper plugin) {
		super(plugin);

		ItemStack[] defaultItems = new ItemStack[41];

		defaultItems[0] = this.plugin.itemManager.item("wizard_staff").item();
		defaultItems[8] = this.plugin.itemManager.item("wizard_spell_selector").item();

		// set the armour to all purple leather
		defaultItems[39] = this.plugin.itemManager.item("wizard_helmet").item();
		defaultItems[38] = this.plugin.itemManager.item("wizard_chestplate").item();
		defaultItems[37] = this.plugin.itemManager.item("wizard_leggings").item();
		defaultItems[36] = this.plugin.itemManager.item("wizard_boots").item();

		this.defaultItems = defaultItems;
	}

	@Override
	public void applyEffects(Player player) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
			.setBaseValue(16);

		// add weakness without using a potion effect
		player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
			.setBaseValue(0.5);

		// add slowness without using a potion effect
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			.setBaseValue(0.1);

		// fully restore health and hunger
		player.setHealth(16);
		player.setFoodLevel(20);
	}

	@Override
	public boolean canModifySlot(int slot) {
		// Do not allow the player to modify the last slot,
		// since it is used to select a spell.
		return slot != 8;
	}

	public void spell(WizardSpell spell) {
		this.spell = spell;
	}

	public WizardSpell spell() {
		return this.spell;
	}

	@Override
	public GameClassType type() {
		return GameClassType.WIZARD;
	}

	@Override
	public ItemStack[] defaultItems() {
		return this.defaultItems;
	}
}
