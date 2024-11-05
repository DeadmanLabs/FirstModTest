package com.quantum.quantum_quarry.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.QuantumQuarry;

public class QuarryBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public QuarryBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuarryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == QuantumQuarry.QUARRY_BLOCK_ENTITY.get() ? (lvl, pos, st, be) -> QuarryBlockEntity.tick(lvl, pos, st, (QuarryBlockEntity)be) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof QuarryBlockEntity blockEntity) {
            if (placer instanceof ServerPlayer player) {
                blockEntity.setOwner(player.getUUID());
            }
        }
    }
}