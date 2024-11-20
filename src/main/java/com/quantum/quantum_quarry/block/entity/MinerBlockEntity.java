package com.quantum.quantum_quarry.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.energy.EnergyStorage;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.quantum_quarry.init.BlockEntities;

public class MinerBlockEntity extends BlockEntity {
    public static final Logger LOGGER = LoggerFactory.getLogger(MinerBlockEntity.class);
    private BlockPos linkedQuarryPos;

    public MinerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MINER_BLOCK_ENTITY.get(), pos, state);
    }

    public void setLinkedQuarryPos(@Nullable BlockPos pos) {
        this.linkedQuarryPos = pos;
        if (this.level != null && !this.level.isClientSide) {
            this.level.invalidateCapabilities(this.worldPosition);
            LOGGER.info("Capabilities invalidated!");
        } else {
            LOGGER.info("Failed to invalidate capabilities! linkedQuarryPos: {}", this.linkedQuarryPos);
        }
    }

    @Nullable
    public BlockPos getLinkedQuarryPos() {
        return this.linkedQuarryPos;
    }
}