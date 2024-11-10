package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;

public class SimpleGenerationChunkHolder extends GenerationChunkHolder {
    private final int ticketLevel;
    private final int queueLevel;

    public SimpleGenerationChunkHolder(ChunkPos pos, int ticketLevel, int queueLevel) {
        super(pos);
        this.ticketLevel = ticketLevel;
        this.queueLevel = queueLevel;
    }

    @Override
    public int getTicketLevel() {
        return this.ticketLevel;
    }
    
    @Override
    public int getQueueLevel() {
        return this.queueLevel;
    }
}
