package com.deepanddeeper.deepanddeeper.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Spawn {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	public Spawn(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static @NotNull Spawn deserialize(Map<String, Object> data) {
		double x = (double) data.get("x");
		double y = (double) data.get("y");
		double z = (double) data.get("z");
		double yaw = (double) data.get("yaw");
		double pitch = (double) data.get("pitch");

		return new Spawn(x, y, z, (float) yaw, (float) pitch);
	}

	public Location location(World world) {
		Location location = new Location(world, this.x, this.y, this.z);

		location.setYaw(this.yaw);
		location.setPitch(this.pitch);

		return location;
	}
}
