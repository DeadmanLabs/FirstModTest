package com.quantum.quantum_quarry;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.FluidState;

import net.neoforged.neoforge.fluids.FluidStack;

public class ChunkMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkMiner.class);

    private final ServerLevel level;
    public final Queue<ItemStack> itemsToGive = new LinkedList<>();
    public final Queue<FluidStack> fluidsToGive = new LinkedList<>();

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
        ServerChunkCache chunkCache = level.getChunkSource();
        chunkCache.addRegionTicket(TicketType.UNKNOWN, pos, 0, pos);
        ChunkAccess chunkAccess = chunkCache.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
        if (chunkAccess instanceof LevelChunk) {
            LOGGER.info("Loaded chunk at position: {}", pos);
            return (LevelChunk)chunkAccess;
        } else {
            LOGGER.warn("Failed to cast LevelChunk to chunkAccess!");
            return null;
        }
    }

    private void unloadChunk(LevelChunk chunk) {
        ServerChunkCache chunkCache = level.getChunkSource();
        chunkCache.removeRegionTicket(TicketType.UNKNOWN, chunk.getPos(), 0, chunk.getPos());
        LOGGER.info("Unloaded chunk at position: {}", chunk.getPos());
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
}
