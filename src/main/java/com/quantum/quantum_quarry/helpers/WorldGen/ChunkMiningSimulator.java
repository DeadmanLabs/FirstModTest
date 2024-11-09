package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;

import java.util.Queue;
import java.util.Random;
import java.util.List;

public class ChunkMiningSimulator {
    public static void simulateMining(ServerLevel world, Queue<ItemStack> itemsToGive) {
        ChunkPos chunkPos = getRandomChunkPos();
        LevelChunk chunk = loadChunk(world, chunkPos);
        if (chunk == null) return;
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), 0, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), world.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            List<ItemStack> drops = Block.getDrops(state, world, pos, null);
            itemsToGive.addAll(drops);
        }
        unloadChunk(world, chunk);
    }

    private static ChunkPos getRandomChunkPos() {
        Random random = new Random();
        int x = random.nextInt(1000000) - 500000;
        int z = random.nextInt(1000000) - 500000;
        return new ChunkPos(x, z);
    }

    private static LevelChunk loadChunk(ServerLevel level, ChunkPos pos) {
        ServerChunkCache chunkCache = level.getChunkSource();
        chunkCache.addRegionTicket(TicketType.UNKNOWN, pos, 0, pos);
        ChunkAccess chunkAccess = chunkCache.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
        if (chunkAccess instanceof LevelChunk) {
            return (LevelChunk)chunkAccess;
        } else {
            return null;
        }
    }

    private static void unloadChunk(ServerLevel level, LevelChunk chunk) {
        ServerChunkCache chunkCache = level.getChunkSource();
        chunkCache.removeRegionTicket(TicketType.UNKNOWN, chunk.getPos(), 0, chunk.getPos());
    }
}
