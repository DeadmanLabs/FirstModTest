package com.example.examplemod;
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

    public TickingBlockEntity(BlockPos pos, BlockState state) {
        super(ExampleMod.TICKING_BLOCK_ENTITY.get(), pos, state);
        LOGGER.info("TickingBlockEntity created at position: {}", pos);
    }

    public void setPlacingPlayerUUID(UUID uuid) {
        this.placingPlayerUUID = uuid;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TickingBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.tickCount++;
            LOGGER.info("Tick count at position {}: {}", pos, blockEntity.tickCount);
            if (blockEntity.tickCount % 20 == 0) {
                if (blockEntity.placingPlayerUUID != null && level instanceof ServerLevel serverLevel) {
                    Player player = serverLevel.getPlayerByUUID(blockEntity.placingPlayerUUID);
                    if (player != null) {
                        player.getInventory().add(new ItemStack(Items.EMERALD));
                        LOGGER.info("Gave 1 emerald to player: {}", player.getName().getString());
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