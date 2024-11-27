package com.quantum.quantum_quarry.init;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegistryManager;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.item.data.VisitedBiomes;

import net.minecraft.resources.ResourceLocation;

public class DataComponents {
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(QuantumQuarry.MODID);
    public static final Codec<VisitedBiomes> VISITED_BIOME_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter(VisitedBiomes::id),
        Codec.list(ResourceKey.codec(Registries.BIOME))
            .xmap(set -> (Set<ResourceKey<Biome>>)new HashSet<>(set), ArrayList::new)
            .fieldOf("biomes")
            .forGetter(VisitedBiomes::biomes)
    ).apply(instance, VisitedBiomes::new));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VisitedBiomes>> VISITED_BIOMES = REGISTRY.registerComponentType(
        "visited_biomes",
        builder -> builder.persistent(VISITED_BIOME_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Biome>>> STORED_BIOME = REGISTRY.registerComponentType(
        "stored_biome", 
        builder -> builder.persistent(ResourceKey.codec(Registries.BIOME))
    );
}
