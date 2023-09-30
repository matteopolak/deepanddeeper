package com.deepanddeeper.deepanddeeper.map;

import com.deepanddeeper.deepanddeeper.weapons.Weapon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Spawn {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public Spawn(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Location location(World world) {
		Location location = new Location(world, this.x, this.y, this.z);

		location.setYaw(this.yaw);
		location.setPitch(this.pitch);

		return location;
	}

	public static @NotNull Spawn deserialize(Map<String, Object> data) {
		double x = (double) data.get("x");
		double y = (double) data.get("y");
		double z = (double) data.get("z");
		double yaw = (double) data.get("yaw");
		double pitch = (double) data.get("pitch");

		return new Spawn(x, y, z, (float) yaw, (float) pitch);
	}
}
