package com.deepanddeeper.deepanddeeper.classes;

import com.deepanddeeper.deepanddeeper.DeepAndDeeper;

public enum GameClassType {
	WIZARD,
	RANGER,
	ROGUE,
	BARBARIAN,
	FIGHTER;

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
			default -> null;
		};
	}

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
}