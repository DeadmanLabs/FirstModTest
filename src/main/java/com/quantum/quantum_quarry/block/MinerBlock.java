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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.init.BlockEntities;
import com.quantum.quantum_quarry.procedures.FindCore;

import io.netty.buffer.Unpooled;

public class MinerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public MinerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockPos quarry = FindCore.execute(world, pos.getX(), pos.getY(), pos.getZ());
            if (quarry != null) {
                boolean isQuarryValid = FindCore.validateStructure(world, quarry);
                if (isQuarryValid) {
                    BlockEntity blockEntity = world.getBlockEntity(quarry);
                    
                } else {
                    if (!world.isClientSide() && world.getServer() != null) {
                        player.displayClientMessage(Component.literal("The Machine is Incomplete!"), true);
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}