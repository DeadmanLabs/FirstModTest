package com.example.examplemod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.item.ItemStack;

import java.util.Queue;
import java.util.LinkedList;

public class ChunkMiner {
    private final ServerPlayer player;
    private final ServerLevel level;
    private final Queue<ItemStack> itemsToGive = new LinkedList<>();

    public ChunkMiner(ServerPlayer player) {
        this.player = null;
        this.level = null;
    }

    public void startMining() {

    }

    private ChunkPos getRandomChunkPos() {
        return null;
    }

    private void generateChunk(ChunkPos pos) {

    }

    private void simulateMining(ChunkPos pos) {

    }

    private void scheduleItemDelivery() {
        
    }
}
