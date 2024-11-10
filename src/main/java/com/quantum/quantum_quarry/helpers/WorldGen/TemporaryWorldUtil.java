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

import java.util.concurrent.CompletableFuture;
import com.google.common.collect.ImmutableList;

import com.quantum.quantum_quarry.helpers.WorldGen.SingleBiomeProvider;
import com.quantum.quantum_quarry.helpers.WorldGen.SimpleGenerationChunkHolder;

public class TemporaryWorldUtil {
    public static ProtoChunk createVoidChunk(ServerLevel level, Holder<Biome> biome, ResourceKey<DimensionType> dimension, int chunkX, int chunkZ) {
        RegistryAccess registryAccess = level.registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        //egistry<NoiseGeneratorSettings> noiseSettingsRegistry = registryAccess.registryOrThrow(Registries.NOISE_SETTINGS);
        BiomeSource biomeSource = new SingleBiomeProvider(biome);
        //Holder<NoiseGeneratorSettings> noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);

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
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

        ProtoChunk protoChunk = new ProtoChunk(
            chunkPos, 
            UpgradeData.EMPTY, 
            level, 
            biomeRegistry, 
            null
        );

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
            registryAccess,
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
        return protoChunk;
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
}
