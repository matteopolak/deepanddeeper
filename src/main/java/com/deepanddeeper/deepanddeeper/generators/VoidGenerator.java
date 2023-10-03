package com.deepanddeeper.deepanddeeper.generators;

import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class VoidGenerator extends ChunkGenerator {
	@Override
	public boolean shouldGenerateSurface() {
		return false;
	}

	@Override
	public boolean shouldGenerateBedrock() {
		return false;
	}

	@Override
	public boolean shouldGenerateCaves() {
		return false;
	}

	@Override
	public boolean shouldGenerateDecorations() {
		return false;
	}

	@Override
	public boolean shouldGenerateMobs() {
		return false;
	}

	@Override
	public boolean shouldGenerateStructures() {
		return false;
	}
}
