package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.Queue;

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

    }
}
