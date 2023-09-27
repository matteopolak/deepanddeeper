package com.deepanddeeper.deepanddeeper;

import com.deepanddeeper.deepanddeeper.events.BlockBreakListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeepAndDeeper extends JavaPlugin {

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
