package com.quantum.quantum_quarry.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

public class RightClicked {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return;
        BlockPos[] positions = {
            BlockPos.containing(x + 1, y, z),
            BlockPos.containing(x - 1, y, z),
            BlockPos.containing(x, y + 1, z),
            BlockPos.containing(x, y - 1, z),
            BlockPos.containing(x, y, z + 1),
            BlockPos.containing(x, y, z - 1)
        };
        BlockPos rootPos = null;
        for (BlockPos pos : positions) {
            if (world.getBlockState(pos).getBlock() == Blocks.QUARRY.get()) {
                rootPos = pos;
                break;
            }
        }
        if (rootPos != null && isSurroundedByMiners(world, rootPos)) {
            updateBlockEntity(world, x, y, z, true);
            openScreen(entity, x, y, z);
        } else {
            broadcastIncompleteMessage(world);
            updateBlockEntity(world, x, y, z, false);
        }
    }

    private static boolean isSurroundedByMiners(LevelAccessor world, BlockPos rootPos) {
        BlockPos[] surroundingPositions = {
            rootPos.above(),
            rootPos.below(),
            rootPos.north(),
            rootPos.south(),
            rootPos.west(),
            rootPos.east()
        };

        for (BlockPos pos : surroundingPositions) {
            if (world.getBlockState(pos).getBlock() != Blocks.MINER.get()) {
                return false;
            }
        }
        return true;
    }

    private static void updateBlockEntity(LevelAccessor world, double x, double y, double z, boolean canOpenInventory) {
        if (!world.isClientSide()) {
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            BlockState blockState = world.getBlockState(pos);
            if (blockEntity != null) {
                blockEntity.getPersistentData().putBoolean("canOpenInventory", canOpenInventory);
            }
            if (world instanceof Level level) {
                level.sendBlockUpdated(pos, blockState, blockState, 3);
            }
        }
    }

    private static void openScreen(Entity entity, double x, double y, double z) {
        if (entity instanceof ServerPlayer player) {
            BlockPos pos = BlockPos.containing(x, y, z);
            NetworkHooks.openScreen(player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("QuantumMinerScreen");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new ScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                }
            }, pos);
        }
    }

    private static void broadcastIncompleteMessage(LevelAccessor world) {
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("The Machine is Incomplete!"), false);
        }
    }
}