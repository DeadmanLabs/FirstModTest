package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;

import org.joml.Vector3f;

public class BiomeGlobeModel implements IDynamicBakedModel {
    private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

    public static final String[][] globe = {
        {
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
            "gggggggggg",
        },
        {
            "ssssssssss",
            "ssssssssss",
            "ssWsssssss",
            "ssssssssss",
            "ssssswppws",
            "sssssd  ps",
            "sssssp  ps",
            "ssssswppws",
            "ssssssssss",
        },
        {
            "          ",
            "          ",
            "  W       ",
            "          ",
            "     wppw ",
            "     D  p ",
            "     p  p ",
            "     wppw ",
            "          ",
        },
        {
            "          ",
            " lll      ",
            " lWl      ",
            " lll      ",
            "     wppw ",
            "     p  p ",
            "     p  p ",
            "     wppw ",
            "          ",
        },
        {
            "          ",
            " lll      ",
            " lWl      ",
            " lll      ",
            "     swws ",
            "     wrrw ",
            "     wrrw ",
            "     swws ",
            "          ",
        },
        {
            "          ",
            " SlS      ",
            " lll      ",
            " SlS      ",
            "      ss  ",
            "     ssss ",
            "     ssss ",
            "      ss  ",
            "          ",
        },
        {
            "          ",
            "  S       ",
            " SlS      ",
            "  S       ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
        },
        {
            "          ",
            "          ",
            "  S       ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
        },
        {
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
        },
        {
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
            "          ",
        }
    };

    private final boolean useAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean usesBlockLight;
    private final TextureAtlasSprite particle;
    private final ItemOverrides overrides;

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
    private static final Map<Character, Block> plainsMap = Map.of(
        'g', Blocks.GRASS_BLOCK,
        's', Blocks.POWDER_SNOW,
        'w', Blocks.OAK_LOG, //pillar / building wood
        'W', Blocks.OAK_LOG,
        'p', Blocks.OAK_PLANKS,
        'D', Blocks.OAK_DOOR, //upper
        'd', Blocks.OAK_DOOR, //lower
        'l', Blocks.OAK_LEAVES,
        'r', Blocks.OAK_PLANKS, //roof blocks
        'S', Blocks.POWDER_SNOW //tree snow
    );
    private static final Map<Character, Block> desertMap = Map.of(
        'g', Blocks.SAND,
        's', Blocks.POWDER_SNOW,
        'w', Blocks.ACACIA_LOG, //pillar / building wood
        'W', Blocks.CACTUS,
        'p', Blocks.ACACIA_PLANKS,
        'D', Blocks.ACACIA_DOOR, //upper
        'd', Blocks.ACACIA_DOOR, //lower
        'l', Blocks.AIR,
        'r', Blocks.ACACIA_PLANKS, //roof blocks
        'S', Blocks.AIR
    );
    private static final Map<Character, Block> netherMap = Map.of(
        'g', Blocks.NETHERRACK,
        's', Blocks.AIR,
        'w', Blocks.NETHER_BRICKS, //pillar / building wood
        'W', Blocks.CRIMSON_STEM,
        'p', Blocks.NETHERRACK,
        'D', Blocks.AIR, //upper
        'd', Blocks.AIR, //lower
        'l', Blocks.NETHER_WART_BLOCK,
        'r', Blocks.GLOWSTONE, //roof blocks
        'S', Blocks.AIR
    );
    private static final Map<Character, Block> soulsandMap = Map.of(
        'g', Blocks.SOUL_SAND,
        's', Blocks.AIR,
        'w', Blocks.RED_NETHER_BRICKS, //pillar / building wood
        'W', Blocks.WARPED_STEM,
        'p', Blocks.SOUL_SOIL,
        'D', Blocks.AIR, //upper
        'd', Blocks.AIR, //lower
        'l', Blocks.WARPED_WART_BLOCK,
        'r', Blocks.SHROOMLIGHT, //roof blocks
        'S', Blocks.AIR
    );
    private static final Map<Character, Block> endMap = Map.of(
        'g', Blocks.END_STONE,
        's', Blocks.AIR,
        'w', Blocks.PURPUR_PILLAR, //pillar / building wood
        'W', Blocks.CHORUS_PLANT,
        'p', Blocks.END_STONE_BRICKS,
        'D', Blocks.AIR, //upper
        'd', Blocks.AIR, //lower
        'l', Blocks.AIR,
        'r', Blocks.END_STONE_BRICKS, //roof blocks
        'S', Blocks.AIR
    );
    private static final Map<Character, Block> basaltMap = Map.of(
        'g', Blocks.BASALT,
        's', Blocks.AIR,
        'w', Blocks.POLISHED_BASALT, //pillar / building wood
        'W', Blocks.MAGMA_BLOCK,
        'p', Blocks.MAGMA_BLOCK,
        'D', Blocks.AIR, //upper
        'd', Blocks.AIR, //lower
        'l', Blocks.BLACKSTONE,
        'r', Blocks.MAGMA_BLOCK, //roof blocks
        'S', Blocks.AIR
    );
    private static final Map<Character, Block> iceMap = Map.of(
        'g', Blocks.ICE,
        's', Blocks.AIR,
        'w', Blocks.PACKED_ICE, //pillar / building wood
        'W', Blocks.PACKED_ICE,
        'p', Blocks.ICE,
        'D', Blocks.AIR, //upper
        'd', Blocks.AIR, //lower
        'l', Blocks.PACKED_ICE,
        'r', Blocks.ICE, //roof blocks
        'S', Blocks.POWDER_SNOW
    );
    private static final Map<Character, Block> oceanMap = Map.ofEntries(
        Map.entry('g', Blocks.GRAVEL),
        Map.entry('s', Blocks.WATER),
        Map.entry('w', Blocks.CHISELED_STONE_BRICKS), //pillar / building wood
        Map.entry('W', Blocks.KELP_PLANT),
        Map.entry('p', Blocks.MOSSY_STONE_BRICKS),
        Map.entry('D', Blocks.WATER), //upper
        Map.entry('d', Blocks.WATER), //lower
        Map.entry('l', Blocks.WATER),
        Map.entry('r', Blocks.MOSSY_STONE_BRICKS), //roof blocks
        Map.entry('S', Blocks.WATER),
        Map.entry(' ', Blocks.WATER)
    );

    public BiomeGlobeModel(BakedModel baseModel, boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle, ItemOverrides overrides) {
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.usesBlockLight = usesBlockLight;
        this.particle = particle;
        this.overrides = overrides;
        this.originalModel = baseModel;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return usesBlockLight;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return originalModel.getQuads(state, side, rand);
        }
        BlockPos playerPos = minecraft.player.blockPosition();
        Holder<Biome> biomeHolder = minecraft.level.getBiome(playerPos);
        Biome biome = biomeHolder.value();
        
        List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, side, rand, extraData, renderType));

        Optional<ResourceKey<Biome>> keyOptional = biomeHolder.unwrapKey();

        if (keyOptional.isPresent()) {
            ResourceKey<Biome> biomeKey = keyOptional.get();
            List<BakedQuad> universeQuads = null;
            if (plains.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, plainsMap, minecraft.level.random);
            } else if (desert.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, desertMap, minecraft.level.random);
            } else if (nether.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, netherMap, minecraft.level.random);
            } else if (soulsand.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, soulsandMap, minecraft.level.random);
            } else if (end.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, endMap, minecraft.level.random);
            } else if (basalt.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, basaltMap, minecraft.level.random);
            } else if (ice.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, iceMap, minecraft.level.random);
            } else if (ocean.contains(biomeKey)) {
                universeQuads = constructUniverse(globe, oceanMap, minecraft.level.random);
            }
        }

        return quads;
    }

    public BakedQuad createQuad(Vector3f from, Vector3f to, Direction direction, TextureAtlasSprite texture) {
        BlockElementFace face = new BlockElementFace(direction, -1, "", new BlockFaceUV(new float[]{ 0, 0, 16, 16 }, 0));
        return new FaceBakery().bakeQuad(from, to, face, texture, direction, null, null, true);
    }

    public List<BakedQuad> constructUniverse(String[][] layers, Map<Character, Block> blockMap, RandomSource rand) {
        List<BakedQuad> quads = new ArrayList<>();
        for (int y = 0; y < layers.length; y++) {
            String[] layer = layers[y];
            for (int x = 0; x < layer.length; x++) {
                String row = layer[x];
                for (int z = 0; z < row.length(); z++) {
                    char c = row.charAt(z);
                    if (c == ' ' && !blockMap.containsKey(' ')) continue;
                    Block block = blockMap.get(c);
                    if (block == null) continue;
                    BlockState blockState = block.defaultBlockState();
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
                    List<BakedQuad> blockQuads = model.getQuads(blockState, null, rand);
                    for (BakedQuad quad : blockQuads) {
                        BakedQuad translatedQuad = translateQuad(quad, x, y, z);
                        quads.add(translatedQuad);
                    }
                }
            }
        }
        return quads;
    }

    private BakedQuad translateQuad(BakedQuad quad, int x, int y, int z) {
        int[] vertexData = quad.getVertices().clone();
        int vertexCount = vertexData.length / 8;
        for (int i = 0; i < vertexCount; i++) {
            int index = i * 8;
            float vx = Float.intBitsToFloat(vertexData[index]);
            float vy = Float.intBitsToFloat(vertexData[index + 1]);
            float vz = Float.intBitsToFloat(vertexData[index + 2]);
            vx += x;
            vy += y;
            vz += z;
            vertexData[index] = Float.floatToRawIntBits(vx);
            vertexData[index + 1] = Float.floatToRawIntBits(vy);
            vertexData[index + 2] = Float.floatToRawIntBits(vz);
        }
        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }
}
