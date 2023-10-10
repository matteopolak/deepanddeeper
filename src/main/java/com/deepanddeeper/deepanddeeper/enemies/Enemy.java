package com.deepanddeeper.deepanddeeper.enemies;

import com.google.common.base.Enums;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Enemy implements ConfigurationSerializable {

	public Enemy(@NotNull EntityType type) {
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return null;
	}

	public @NotNull Enemy deserialize(Map<String, Object> data) {
		return new Enemy(Enums.getIfPresent(EntityType.class, (String) data.get("type")).orNull());
	}
}