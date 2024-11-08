package com.quantum.quantum_quarry.item.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public record VisitedBiomes (Set<ResourceKey<Biome>> biomes) {}
