package com.quantum.quantum_quarry.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.Containers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.init.BlockEntities;
import com.quantum.quantum_quarry.procedures.FindCore;
import com.quantum.quantum_quarry.world.inventory.ScreenMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;

public class QuarryBlock extends Block implements EntityBlock {
    public static final Logger LOGGER = LoggerFactory.getLogger(MinerBlock.class);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public QuarryBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuarryBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == BlockEntities.QUARRY_BLOCK_ENTITY.get() ? (lvl, pos, st, be) -> QuarryBlockEntity.tick(lvl, pos, st, (QuarryBlockEntity)be) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof QuarryBlockEntity blockEntity) {
            if (placer instanceof ServerPlayer player) {
                blockEntity.setOwner(player.getUUID());
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        super.useWithoutItem(state, world, pos, player, hit);
        if (!world.isClientSide) {
            boolean isQuarryValid = FindCore.validateStructure(world, pos);
            if (isQuarryValid) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (player instanceof ServerPlayer serverPlayer && blockEntity instanceof QuarryBlockEntity quarryEntity) {
                    serverPlayer.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return quarryEntity.getDisplayName();
                        }
                        @Override
                        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                            return new ScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                        }
                    }, pos);
                }
            } else {
                player.displayClientMessage(Component.literal("The Machine is Incomplete!"), true);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof QuarryBlockEntity quarryEntity) {
                Containers.dropContents(level, pos, quarryEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof QuarryBlockEntity quarryEntity) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(quarryEntity);
        } else {
            return 0;
        }
    }
}