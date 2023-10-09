package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;

public enum GameClassType {
	WIZARD,
	RANGER,
	ROGUE,
	BARBARIAN,
	FIGHTER;

	public static GameClassType from(String string) {
		return switch (string) {
			case "wizard" -> WIZARD;
			case "ranger" -> RANGER;
			case "rogue" -> ROGUE;
			case "barbarian" -> BARBARIAN;
			case "fighter" -> FIGHTER;
			default -> null;
		};
	}

	public String filename() {
		return switch (this) {
			case WIZARD -> "wizard";
			case RANGER -> "ranger";
			case ROGUE -> "rogue";
			case BARBARIAN -> "barbarian";
			case FIGHTER -> "fighter";
		};
	}

	public String colouredName() {
		return switch (this) {
			case WIZARD -> "§5Wizard";
			case RANGER -> "§2Ranger";
			case ROGUE -> "§8Rogue";
			case BARBARIAN -> "§cBarbarian";
			case FIGHTER -> "§6Fighter";
		};
	}

	public GameClass getGameClass(DeepAndDeeper plugin) {
		return switch (this) {
			case WIZARD -> new WizardClass(plugin);
			case FIGHTER -> new FighterClass(plugin);
			default -> null;
		};
	}

	public static GameClassType fromId(int id) {
		return switch (id) {
			case 0 -> WIZARD;
			case 1 -> RANGER;
			case 2 -> ROGUE;
			case 3 -> BARBARIAN;
			case 4 -> FIGHTER;
			default -> null;
		};
	}

	public int id() {
		return switch (this) {
			case WIZARD -> 0;
			case RANGER -> 1;
			case ROGUE -> 2;
			case BARBARIAN -> 3;
			case FIGHTER -> 4;
		};
	}
}