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
    private static final List<ResourceKey<Biome>> plains = List.of(
    	Biomes.PLAINS,
    	Biomes.BAMBOO_JUNGLE,
    	Biomes.BIRCH_FOREST,
    	Biomes.CHERRY_GROVE,
    	Biomes.DARK_FOREST,
    	Biomes.FOREST,
    	Biomes.GROVE,
    	Biomes.JUNGLE,
    	Biomes.BEACH,
    	Biomes.SNOWY_BEACH,
    	Biomes.SNOWY_PLAINS,
    	Biomes.SPARSE_JUNGLE,
    	Biomes.WINDSWEPT_HILLS,
    	Biomes.WINDSWEPT_GRAVELLY_HILLS,
    	Biomes.WINDSWEPT_FOREST,
    	Biomes.TAIGA,
    	Biomes.SWAMP,
    	Biomes.SUNFLOWER_PLAINS,
    	Biomes.STONY_SHORE,
    	Biomes.STONY_PEAKS,
    	Biomes.SNOWY_TAIGA,
    	Biomes.SNOWY_SLOPES,
    	Biomes.DEEP_DARK,
    	Biomes.DRIPSTONE_CAVES,
    	Biomes.FLOWER_FOREST,
    	Biomes.JAGGED_PEAKS,
    	Biomes.LUSH_CAVES,
    	Biomes.MANGROVE_SWAMP,
    	Biomes.MEADOW,
    	Biomes.MUSHROOM_FIELDS,
    	Biomes.OLD_GROWTH_BIRCH_FOREST,
    	Biomes.OLD_GROWTH_PINE_TAIGA,
    	Biomes.OLD_GROWTH_SPRUCE_TAIGA
    );
    private static final List<ResourceKey<Biome>> desert = List.of(
        Biomes.BADLANDS,
        Biomes.WOODED_BADLANDS,
        Biomes.SAVANNA,
        Biomes.SAVANNA_PLATEAU,
        Biomes.DESERT,
        Biomes.WINDSWEPT_SAVANNA,
        Biomes.ERODED_BADLANDS
    );
    private static final List<ResourceKey<Biome>> nether = List.of(
	    Biomes.NETHER_WASTES,
        Biomes.WARPED_FOREST,
        Biomes.CRIMSON_FOREST
    );
    private static final List<ResourceKey<Biome>> soulsand = List.of(
    	Biomes.SOUL_SAND_VALLEY
    );
    private static final List<ResourceKey<Biome>> end = List.of(
	    Biomes.END_BARRENS,
	    Biomes.END_HIGHLANDS,
	    Biomes.END_MIDLANDS,
        Biomes.THE_END,
        Biomes.THE_VOID,
        Biomes.SMALL_END_ISLANDS
    );
    private static final List<ResourceKey<Biome>> basalt = List.of(
	    Biomes.BASALT_DELTAS
    );
    private static final List<ResourceKey<Biome>> ice = List.of(
	    Biomes.FROZEN_PEAKS,
	    Biomes.FROZEN_RIVER,
        Biomes.ICE_SPIKES
    );
    private static final List<ResourceKey<Biome>> ocean = List.of(
    	Biomes.DEEP_COLD_OCEAN,
    	Biomes.DEEP_FROZEN_OCEAN,
    	Biomes.DEEP_LUKEWARM_OCEAN,
        Biomes.DEEP_OCEAN,
        Biomes.COLD_OCEAN,
        Biomes.FROZEN_OCEAN,
        Biomes.OCEAN,
        Biomes.RIVER,
        Biomes.WARM_OCEAN,
        Biomes.LUKEWARM_OCEAN
    );

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
