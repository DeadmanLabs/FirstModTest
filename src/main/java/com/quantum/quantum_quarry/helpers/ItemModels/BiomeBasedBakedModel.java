package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.neoforge.client.model.data.ModelData;

public class BiomeBasedBakedModel implements BakedModel {
    private final BakedModel originalModel;
    private static final List<ResourceKey<Biome>> plains = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> desert = new ArrayList<>() {
        Biomes.BADLANDS,
    };
    private static final List<ResourceKey<Biome>> nether = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> soulsand = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> end = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> basalt = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> ice = new ArrayList<>() {

    };
    private static final List<ResourceKey<Biome>> ocean = new ArrayList<>() {

    };

    public BiomeBasedBakedModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return originalModel.getQuads(state, side, rand);
        }
        BlockPos playerPos = minecraft.player.blockPosition();
        Holder<Biome> biomeHolder = minecraft.level.getBiome(playerPos);
        Biome biome = biomeHolder.value();

        List<BakedQuad> quads = new ArrayList<>();

        switch (biome) {
            case Biomes.BADLANDS:
                break;
            case Biomes.BAMBOO_JUNGLE:
                break;
            case Biomes.BASALT_DELTAS:
            break;
            case Biomes.BEACH:
                break;
            case Biomes.BIRCH_FOREST:
                break;
            case Biomes.CHERRY_GROVE:
                break;
            case Biomes.COLD_OCEAN:
                break;
            case Biomes.CRIMSON_FOREST:
                break;
            case Biomes.DARK_FOREST:
                break;
            case Biomes.DEEP_COLD_OCEAN:
                break;
            case Biomes.DEEP_DARK:
                break;
            case Biomes.DEEP_FROZEN_OCEAN:
                break;
            case Biomes.DEEP_LUKEWARM_OCEAN:
                break;
            case Biomes.DEEP_OCEAN:
                break;
            case Biomes.DESERT:
                break;
            case Biomes.DRIPSTONE_CAVES:
                break;
            case Biomes.END_BARRENS:
                break;
            case Biomes.END_HIGHLANDS:
                break;
            case Biomes.END_MIDLANDS:
                break;
            case Biomes.ERODED_BADLANDS:
                break;
            case Biomes.FLOWER_FOREST:
                break;
            case Biomes.FOREST:
                break;
            case Biomes.FROZEN_OCEAN:
                break;
            case Biomes.FROZEN_PEAKS:
                break;
            case Biomes.FROZEN_RIVER:
                break;
            case Biomes.GROVE:
                break;
            case Biomes.ICE_SPIKES:
                break;
            case Biomes.JAGGED_PEAKS:
                break;
            case Biomes.JUNGLE:
                break;
            case Biomes.LUKEWARM_OCEAN:
                break;
            case Biomes.LUSH_CAVES:
                break;
            case Biomes.MANGROVE_SWAMP:
                break;
            case Biomes.MEADOW:
                break;
            case Biomes.MUSHROOM_FIELDS:
                break;
            case Biomes.NETHER_WASTES:
                break;
            case Biomes.OCEAN:
                break;
            case Biomes.OLD_GROWTH_BIRCH_FOREST:
                break;
            case Biomes.OLD_GROWTH_PINE_TAIGA:
                break;
            case Biomes.OLD_GROWTH_SPRUCE_TAIGA:
                break;
            case Biomes.PLAINS:
                break;
            case Biomes.RIVER:
                break;
            case Biomes.SAVANNA:
                break;
            case Biomes.SAVANNA_PLATEAU:
                break;
            case Biomes.SMALL_END_ISLANDS:
                break;
            case Biomes.SNOWY_BEACH:
                break;
            case Biomes.SNOWY_PLAINS:
                break;
            case Biomes.SNOWY_SLOPES:
                break;
            case Biomes.SNOWY_TAIGA:
                break;
            case Biomes.SOUL_SAND_VALLEY:
                break;
            case Biomes.SPARSE_JUNGLE:
                break;
            case Biomes.STONY_PEAKS:
                break;
            case Biomes.STONY_SHORE:
                break;
            case Biomes.SUNFLOWER_PLAINS:
                break;
            case Biomes.SWAMP:
                break;
            case Biomes.TAIGA:
                break;
            case Biomes.THE_END:
                break;
            case Biomes.THE_VOID:
                break;
            case Biomes.WARM_OCEAN:
                break;
            case Biomes.WARPED_FOREST:
                break;
            case Biomes.WINDSWEPT_FOREST:
                break;
            case Biomes.WINDSWEPT_GRAVELLY_HILLS:
                break;
            case Biomes.WINDSWEPT_HILLS:
                break;
            case Biomes.WINDSWEPT_SAVANNA:
                break;
            case Biomes.WOODED_BADLANDS:
                break;
            default:

                break;
        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return originalModel.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }
}
