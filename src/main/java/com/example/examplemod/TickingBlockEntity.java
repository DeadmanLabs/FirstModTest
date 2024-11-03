package com.example.examplemod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TickingBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(TickingBlockEntity.class);
    private int tickCount = 0;

    public TickingBlockEntity(BlockPos pos, BlockState state) {
        super(ExampleMod.TICKING_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("TickingBlockEntity created at position: {}", pos);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TickingBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.tickCount++;
            LOGGER.info("Tick count at position {}: {}", pos, blockEntity.tickCount);
        }
    }
}