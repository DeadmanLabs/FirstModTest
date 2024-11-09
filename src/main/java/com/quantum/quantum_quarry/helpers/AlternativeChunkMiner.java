
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.resources.ResourceKey;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerChunkCache;

public class SingleBiomeProvider extends BiomeSource {
    private final Holder<Biome> biome;

    public SingleBiomeProvider(Holder<Biome> biome) {
        super(BiomeSources.BIOME_PROVIDER_CODEC);
        this.biome = biome;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return BiomeSources.BIOME_PROVIDER_CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z) {
        return biome;
    }
}

public class TemporaryWorldUtil {
    public static ServerLevel createTemporaryWorld(MinecraftServer server, Holder<Biome> biome, DimensionType dimension = DimensionType.DEFAULT_OVERWORLD) {
        ChunkGenerator chunkGenerator = new ChunkGenerator(
            new SingleBiomeProvider(biome),
            dimension.getBaseCubicNoise(),
            true
        );
        ServerLevel tempWorld = new ServerLevel(
            server, server.executor, server.storageSource, server.registryHolder,
            ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("temporary_dimension")),
            dimension, chunkGenerator,
            server.getStructureManager(), false, BiomeManager.BiomeLayer.PRECISE, 0L
        );
        return tempWorld;
    }
}

public class ChunkMiningSimulator {
    public static void simulateMining(ServerLevel world, Queue<ItemStack> itemsToGive) {
        ChunkPos chunkPos = new ChunkPos(world.random.nextInt(1000), world.random.nextInt(1000));
        ServerChunkCache chunkCache = world.getChunkSource();
        LevelChunk chunk = chunkCache.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        if (chunk == null) return;
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), world.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            List<ItemStack> drops = Block.getDrops(state, world, pos, null);
            itemsToGive.addAll(drops);
        }
        chunkCache.removeRegionTicket(TicketType.UNKNOWN, chunkPos, 0, chunkPos);
    }
}

public class ChunkMiner {
    private final ServerLevel mainWorld;
    private final MinecraftServer server;
    private final Holder<Biome> biome;
    public final Queue<ItemStack> itemsToGive = new LinkedList<>();

    public ChunkMiner(ServerLevel level, Holder<Biome> biome) {
        this.mainWorld = level;
        this.server = level.getServer();
        this.biome = biome;
    }

    public void startMining() {
        ServerLevel tempWorld = TemporaryWorldUtil.createTemporaryWorld(server, biome);
        ChunkMiningSimulator.simulateMining(tempWorld, itemsToGive);
        // Destroy temp world
    }
}