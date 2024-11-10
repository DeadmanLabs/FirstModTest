package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

import com.quantum.quantum_quarry.helpers.WorldGen.SingleBiomeProvider;

public class TemporaryWorldUtil {
    /*
    public static ProtoChunk createVoidChunk(ServerLevel level, Holder<Biome> biome, ResourceKey<DimensionType> dimension, int chunkX, int chunkZ) {
        RegistryAccess registryAccess = level.registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        //egistry<NoiseGeneratorSettings> noiseSettingsRegistry = registryAccess.registryOrThrow(Registries.NOISE_SETTINGS);
        BiomeSource biomeSource = new SingleBiomeProvider(biome);
        //Holder<NoiseGeneratorSettings> noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);

        Registry<NoiseGeneratorSettings> noiseSettingsRegistry = level.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS);
        Holder<NoiseGeneratorSettings> noiseSettings = null;
        switch (dimension) {
            case BuiltinDimensionTypes.OVERWORLD:
                noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
                break;
            case BuiltinDimensionTypes.NETHER:
                noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.NETHER);
                break;
            case BuiltinDimensionTypes.END:
                noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.END);
                break;
            default:
                noiseSettings = noiseSettingsRegistry.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
                break;
        }

        ChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(biomeSource, noiseSettings);
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

        ProtoChunk protoChunk = new ProtoChunk(
            chunkPos, 
            UpgradeData.EMPTY, 
            level, 
            biomeRegistry, 
            null
        );
        chunkGenerator.fillFromNoise(
            Blender,
            RandomState,
            StructureManager,
            protoChunk
        );
        chunkGenerator.applyCarvers(
            WorldGenRegion,
            long,
            RandomState,
            BiomeManager,
            StructureManager,
            protoChunk,
            GenerationStep.Carving
        );
        chunkGenerator.buildSurface(
            WorldGenRegion,
            StructureManager,
            RandomState,
            protoChunk
        );
        chunkGenerator.createStructures(
            RegistryAccess,
            ChunkGeneratorStructureState,
            StructureManager,
            protoChunk,
            StructureTemplateManager
        );
        chunkGenerator.applyBiomeDecoration(
            WorldGenLevel,
            protoChunk,
            StructureManager
        );
        return protoChunk;
    }
    */
}
