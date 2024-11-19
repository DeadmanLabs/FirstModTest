package com.quantum.quantum_quarry.item.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;
import java.util.UUID;

public record VisitedBiomes (UUID id, Set<ResourceKey<Biome>> biomes) {}
