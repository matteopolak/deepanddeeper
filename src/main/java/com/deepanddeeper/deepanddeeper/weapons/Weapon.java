package com.deepanddeeper.deepanddeeper.weapons;

import com.google.common.base.Enums;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Weapon {
	private String id;
	private TextComponent name;
	private List<TextComponent> lore;

	private double damage = 10.f;
	private double cooldown = 2.4f;

	public Weapon(@NotNull String id) {
		this.id = id;
	}

	public String id() {
		return this.id;
	}

	public Weapon name(TextComponent name) {
		this.name = name;
		return this;
	}

	public Weapon lore(List<TextComponent> lore) {
		this.lore = lore;
		return this;
	}

	public Weapon damage(double damage) {
		this.damage = damage;
		return this;
	}

	public Weapon cooldown(double cooldown) {
		this.cooldown = cooldown;
		return this;
	}

	public static @NotNull Weapon deserialize(Map<String, Object> data) {
		String id = (String) data.get("id");
		TextComponent name = Component.text((String) data.get("name"));
		List<TextComponent> lore = ((List<String>) data.get("lore"))
			.stream()
			.map(Component::text)
			.toList();
		double damage = (double) data.get("damage");
		double cooldown = (double) data.get("cooldown");

		return new Weapon(id)
			.name(name)
			.lore(lore)
			.damage(damage)
			.cooldown(cooldown);
	}
}