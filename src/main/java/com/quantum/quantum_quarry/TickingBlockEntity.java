package com.quantum.quantum_quarry;
import javax.annotation.Nullable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class TickingBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(TickingBlockEntity.class);
    private int tickCount = 0;
    private UUID placingPlayerUUID;
    public ChunkMiner manager;

    public TickingBlockEntity(BlockPos pos, BlockState state) {
        super(QuantumQuarry.TICKING_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("TickingBlockEntity created at position: {}", pos);
    }

    public void setPlacingPlayerUUID(UUID uuid) {
        this.placingPlayerUUID = uuid;
        if (this.level instanceof ServerLevel serverLevel) {
            this.manager = new ChunkMiner(serverLevel);
        } else {
            LOGGER.info("Set owner but could not get instance of ServerLevel");
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TickingBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.tickCount++;
            //LOGGER.info("Tick count at position {}: {}", pos, blockEntity.tickCount);
                if (blockEntity.placingPlayerUUID != null && level instanceof ServerLevel serverLevel && blockEntity.manager != null) {
                    Player player = serverLevel.getPlayerByUUID(blockEntity.placingPlayerUUID);
                    if (player != null) {
                        if (blockEntity.manager.itemsToGive.size() <= 0) {
                            blockEntity.manager.startMining();
                        }
                        ItemStack item = blockEntity.manager.itemsToGive.poll();
                        if (item != null) {
                            player.getInventory().add(item);
                        }
                    }
                }
        }
    }

    // We use the below functions to keep block info after its been broken. Very useful for blocks that have an owner
    @Override
    protected void saveAdditional(CompoundTag tag, Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("TickCount", this.tickCount);
        if (this.placingPlayerUUID != null) {
            tag.putUUID("PlacingPlayerUUID", this.placingPlayerUUID);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider provider) {
        super.loadAdditional(tag, provider);
        this.tickCount = tag.getInt("TickCount");
        if (tag.hasUUID("PlacingPlayerUUID")) {
            this.placingPlayerUUID = tag.getUUID("PlacingPlayerUUID");
        }
    }
}