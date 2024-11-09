package com.quantum.quantum_quarry.helpers.WorldGen;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;
import java.util.stream.Collectors;

public class SingleBiomeProvider extends BiomeSource {
    public static final MapCodec<SingleBiomeProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Biome.CODEC.fieldOf("biome").forGetter(SingleBiomeProvider::getBiome)
        ).apply(instance, SingleBiomeProvider::new)
    );
    private final Holder<Biome> biome;

    public SingleBiomeProvider(Holder<Biome> biome) {
        super();
        this.biome = biome;
    }

    public Holder<Biome> getBiome() {
        return biome;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return biome;
    }

    @Override
    public Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.possibleBiomes().stream();
    }
}
