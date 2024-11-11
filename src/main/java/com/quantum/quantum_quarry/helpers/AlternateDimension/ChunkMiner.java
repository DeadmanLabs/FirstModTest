package com.quantum.quantum_quarry.helpers.AlternateDimension;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class ChunkMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkMiner.class);

    private final ServerLevel level;
    public final Queue<ItemStack> itemsToGive = new LinkedList<>();
    public final Queue<FluidStack> fluidsToGive = new LinkedList<>();
    public Holder<Biome> currentBiome = null;
    public ResourceKey<DimensionType> dimension = null;

    public ChunkMiner(ServerLevel level) {
        this.level = level;
    }

    public void startMining() {
        ChunkPos randomPos = getRandomChunkPos();
        LevelChunk chunk = loadChunk(randomPos);
        if (chunk != null) {
            simulateMining(chunk);
            unloadChunk(chunk);
        }
    }

    private ChunkPos getRandomChunkPos() {
        Random random = new Random();
        int x = random.nextInt(1000000) - 500000;
        int z = random.nextInt(1000000) - 500000;
        return new ChunkPos(x, z);
    }

    private LevelChunk loadChunk(ChunkPos pos) {
        ResourceKey<Level> dimensionKey = getRandomDimension();
        ServerLevel targetLevel = level.getServer().getLevel(dimensionKey);
        if (targetLevel == null) {
            LOGGER.warn("Dimension {} not found!", dimensionKey.location());
            return null;
        }
        ServerChunkCache chunkCache = targetLevel.getChunkSource();
        chunkCache.addRegionTicket(TicketType.UNKNOWN, pos, 0, pos);
        ChunkAccess chunkAccess = chunkCache.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
        if (chunkAccess instanceof LevelChunk) {
            LOGGER.info("Loaded chunk at position: {} in dimension: {}", pos, dimensionKey);
            return (LevelChunk)chunkAccess;
        } else {
            LOGGER.warn("Failed to cast LevelChunk to chunkAccess!");
            return null;
        }
    }

    private void unloadChunk(LevelChunk chunk) {
        Level targetLevel = chunk.getLevel();
        ServerChunkCache chunkCache = level.getChunkSource();
        chunkCache.removeRegionTicket(TicketType.UNKNOWN, chunk.getPos(), 0, chunk.getPos());
        LOGGER.info("Unloaded chunk at position: {} in dimension: {}", chunk.getPos(), targetLevel.dimension().location());
    }

    private void simulateMining(LevelChunk chunk, int fortune) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                }
                else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                        if (fortune > 0) {
                            List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, null, getFortuneTool(level, fortune));
                            itemsToGive.addAll(drops);
                        }
                }
            }
        }
    }

    private void simulateSilktouchMining(LevelChunk chunk) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                }
                else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, null, getSilkTool(level));
                    itemsToGive.addAll(drops);
                }
            }
        }
    }

    private void simulateMining(LevelChunk chunk) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                }
                else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity);
                    itemsToGive.addAll(drops);
                }
            }
        }
    }

    private ItemStack getFortuneTool(ServerLevel level, int fortuneLevel) {
        
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        ResourceKey<Enchantment> fortuneKey = Enchantments.FORTUNE;
        Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> fortuneHolder = enchantmentRegistry.getHolder(fortuneKey);
        fortuneHolder.ifPresent(holder -> tool.enchant(holder, fortuneLevel));
        return tool;
    }

    private ItemStack getSilkTool(ServerLevel level) {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        ResourceKey<Enchantment> silktouchKey = Enchantments.SILK_TOUCH;
        Registry<Enchantment> enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> silktouchHolder = enchantmentRegistry.getHolder(silktouchKey);
        silktouchHolder.ifPresent(holder -> tool.enchant(holder, 1));
        return tool;
    }

    private ResourceKey<Level> getRandomDimension() {
        List<ResourceKey<Level>> dimensions = Arrays.asList(
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("quantum_quarry", "overworld_dimension_mock")),
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("quantum_quarry", "nether_dimension_mock")),
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("quantum_quarry", "end_dimension_mock"))
        );
        Random random = new Random();
        return dimensions.get(random.nextInt(dimensions.size()));
    }
}
