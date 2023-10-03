package com.deepanddeeper.deepanddeeper.weapons;

import java.util.List;

public class WeaponHolder {
	private List<Weapon> weapons;

	public WeaponHolder(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	public List<Weapon> weapons() {
		return this.weapons;
	}
}
