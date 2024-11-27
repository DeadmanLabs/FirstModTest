package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderGetter;
import net.minecraft.util.StaticCache2D;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.status.ChunkDependencies;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStatusTask;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;
import net.minecraft.tags.BiomeTags;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;

import com.quantum.quantum_quarry.helpers.WorldGen.SingleBiomeProvider;
import com.quantum.quantum_quarry.helpers.WorldGen.SimpleGenerationChunkHolder;

public class TemporaryWorldUtil {
    public static ProtoChunk createVoidChunk(ServerLevel level, Holder<Biome> biome) {
        if (biome == null) {
            biome = randomBiome(level);
        }
        ResourceKey<DimensionType> dimension = getDimensionFromBiome(level.registryAccess(), biome);
        RegistryAccess registryAccess = level.registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        BiomeSource biomeSource = new SingleBiomeProvider(biome);

        Registry<NoiseGeneratorSettings> noiseSettingsRegistry = level.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS);
        Holder<NoiseGeneratorSettings> noiseSettings = null;
        if (dimension == BuiltinDimensionTypes.NETHER) {
            noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.NETHER);
        } else if (dimension == BuiltinDimensionTypes.END) {
            noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.END);
        } else {
            noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
        }
        HolderGetter<NoiseParameters> noiseParameters = registryAccess.lookupOrThrow(Registries.NOISE);

        ChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(biomeSource, noiseSettings);
        ChunkPos chunkPos = getRandomChunkPos();
        Map<ChunkPos, ProtoChunk> chunkMap = new HashMap<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkPos pos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                ProtoChunk protoChunk = new ProtoChunk(
                    pos, 
                    UpgradeData.EMPTY, 
                    level, 
                    biomeRegistry, 
                    null
                );
                chunkMap.put(pos, protoChunk);
            }
        }
        for (ProtoChunk protoChunk : chunkMap.values()) {
            generateChunk(level, chunkGenerator, noiseSettings, noiseParameters, protoChunk, chunkMap);
        }
        return chunkMap.get(chunkPos);
    }

    private static void generateChunk(ServerLevel level, ChunkGenerator chunkGenerator, Holder<NoiseGeneratorSettings> noiseSettings, HolderGetter<NoiseParameters> noiseParameters, ProtoChunk protoChunk, Map<ChunkPos, ProtoChunk> chunkMap) {
        long seed = level.getSeed();
        RandomState randomState = RandomState.create(noiseSettings.value(), noiseParameters, level.getSeed());
        StructureManager structureManager = level.structureManager();
        BiomeManager biomeManager = new BiomeManager(level, seed);
        WorldGenRegion worldGenRegion = genRegion(level, protoChunk);
        Blender blender = Blender.of(worldGenRegion);

        chunkGenerator.fillFromNoise(
            blender,
            randomState,
            structureManager,
            protoChunk
        );
        chunkGenerator.applyCarvers(
            worldGenRegion,
            seed,
            randomState,
            biomeManager,
            structureManager,
            protoChunk,
            GenerationStep.Carving.AIR
        );
        chunkGenerator.applyCarvers(
            worldGenRegion,
            seed,
            randomState,
            biomeManager,
            structureManager,
            protoChunk,
            GenerationStep.Carving.LIQUID
        );
        chunkGenerator.buildSurface(
            worldGenRegion,
            structureManager,
            randomState,
            protoChunk
        );
        chunkGenerator.createStructures(
            level.registryAccess(),
            level.getChunkSource().getGeneratorState(),
            structureManager,
            protoChunk,
            level.getStructureManager()
        );
        chunkGenerator.applyBiomeDecoration(
            level,
            protoChunk,
            structureManager
        );
    }

    private static WorldGenRegion genRegion(ServerLevel level, ProtoChunk chunk) {
        StaticCache2D.Initializer<GenerationChunkHolder> initializer = (x, z) -> {
            ChunkPos pos = new ChunkPos(x, z);
            return new SimpleGenerationChunkHolder(pos, 0, 0);
        };
        StaticCache2D<GenerationChunkHolder> chunkCache = StaticCache2D.create(
            chunk.getPos().x,
            chunk.getPos().z,
            0,
            initializer
        );
        ImmutableList<ChunkStatus> dependenciesList = ImmutableList.of(ChunkStatus.FULL);
        ChunkDependencies chunkDependencies = new ChunkDependencies(dependenciesList);
        ChunkStatusTask task = (context, step, cache, genChunk) -> CompletableFuture.completedFuture(genChunk);
        ChunkStep chunkStep = new ChunkStep(
            ChunkStatus.FULL, 
            chunkDependencies, 
            chunkDependencies, 
            0, 
            task
        );
        return new WorldGenRegion(level, chunkCache, chunkStep, chunk);
    }

    public static ResourceKey<DimensionType> getDimensionFromBiome(RegistryAccess registryAccess, Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_OVERWORLD)) {
            return BuiltinDimensionTypes.OVERWORLD;
        } else if (biome.is(BiomeTags.IS_NETHER)) {
            return BuiltinDimensionTypes.NETHER;
        } else if (biome.is(BiomeTags.IS_END)) {
            return BuiltinDimensionTypes.END;
        }
        return null;
    };

    private static ChunkPos getRandomChunkPos() {
        Random random = new Random();
        int x = random.nextInt(1000000) - 500000;
        int z = random.nextInt(1000000) - 500000;
        return new ChunkPos(x, z);
    }

    private static Holder<Biome> randomBiome(ServerLevel level) {
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        List<Holder<Biome>> allBiomes = biomeRegistry.holders().collect(Collectors.toList());

        List<Holder<Biome>> dimensionBiomes = allBiomes.stream()
            .filter(biomeHolder -> {
                ResourceKey<DimensionType> dimension = getDimensionFromBiome(level.registryAccess(), biomeHolder);
                if (dimension == BuiltinDimensionTypes.OVERWORLD || dimension == BuiltinDimensionTypes.NETHER || dimension == BuiltinDimensionTypes.END) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

        if (!dimensionBiomes.isEmpty()) {
            Random random = new Random();
            return dimensionBiomes.get(random.nextInt(allBiomes.size()));
        } else {
            return null; //never gonna happen
        }
    }
}
