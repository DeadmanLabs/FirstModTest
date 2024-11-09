package com.quantum.quantum_quarry.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.quantum.quantum_quarry.init.ModBlocks;

public class FindCore {
    public static BlockPos execute(LevelAccessor world, double x, double y, double z) {
        BlockPos quarryPos = null;
        boolean rootFound = false;
        BlockPos[] positions = {
            BlockPos.containing(x, y, z),
            BlockPos.containing(x + 1, y, z),
            BlockPos.containing(x - 1, y, z),
            BlockPos.containing(x, y + 1, z),
            BlockPos.containing(x, y - 1, z),
            BlockPos.containing(x, y, z + 1),
            BlockPos.containing(x, y, z - 1)
        };
        for (BlockPos pos : positions) {
            if (world.getBlockState(pos).getBlock() == ModBlocks.QUARRY.get()) {
                quarryPos = pos;
                rootFound = true;
                break;
            }
        }
        if (rootFound) {
            return quarryPos;
        } else {
            return null;
        }
    }

    public static boolean validateStructure(LevelAccessor world, BlockPos pos) {
        BlockPos[] positions = {
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() - 1)
        };
        boolean valid = true;
        for (BlockPos sidePos : positions) {
            valid = valid && (world.getBlockState(sidePos).getBlock() == ModBlocks.MINER.get());
        }
        return valid;
    }

    public static BlockPos[] findStorage(Level world, BlockPos pos) {
        BlockPos[] positions = {
            BlockPos.containing(pos.getX() + 1 + 1, pos.getY(), pos.getZ()), // ignore - 1 on the X cause that is the quarry face
            BlockPos.containing(pos.getX() + 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() + 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() - 1 - 1, pos.getY(), pos.getZ()), // ignore + 1 on the X cause that is the quarry face
            BlockPos.containing(pos.getX() - 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() + 1 + 1, pos.getZ()), // ignore - 1 on the Y cause that is the quarry face
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() - 1 - 1, pos.getZ()), // ignore + 1 on the Y cause that is the quarry face
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() + 1 + 1), // ignore - 1 on the Z cause that is the quarry face
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() - 1 - 1), // ignore + 1 on the Z cause that is the quarry face
        };
        return Arrays.asList(positions).stream().filter(face -> {
            return world.getCapability(Capabilities.ItemHandler.BLOCK, face, Direction.UP) != null;
        }).collect(Collectors.toList()).toArray(new BlockPos[0]);
    }

    public static BlockPos[] findFluidStorage(Level world, BlockPos pos) {
        BlockPos[] positions = {
            BlockPos.containing(pos.getX() + 1 + 1, pos.getY(), pos.getZ()), // ignore - 1 on the X cause that is the quarry face
            BlockPos.containing(pos.getX() + 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() + 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() - 1 - 1, pos.getY(), pos.getZ()), // ignore + 1 on the X cause that is the quarry face
            BlockPos.containing(pos.getX() - 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() + 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() + 1 + 1, pos.getZ()), // ignore - 1 on the Y cause that is the quarry face
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX() - 1, pos.getY() - 1, pos.getZ()),
            BlockPos.containing(pos.getX(), pos.getY() - 1 - 1, pos.getZ()), // ignore + 1 on the Y cause that is the quarry face
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() + 1),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() + 1 + 1), // ignore - 1 on the Z cause that is the quarry face
            BlockPos.containing(pos.getX() + 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX() - 1, pos.getY(), pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY() + 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY() - 1, pos.getZ() - 1),
            BlockPos.containing(pos.getX(), pos.getY(), pos.getZ() - 1 - 1), // ignore + 1 on the Z cause that is the quarry face
        };
        return Arrays.asList(positions).stream().filter(face -> {
            return world.getCapability(Capabilities.FluidHandler.BLOCK, face, Direction.UP) != null;
        }).collect(Collectors.toList()).toArray(new BlockPos[0]);
    }

    public static boolean insertItem(Level world, BlockPos pos, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            IItemHandler itemHandler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
            if (itemHandler != null) {
                for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                    ItemStack remainingStack = itemHandler.insertItem(slot, stack, false);
                    if (remainingStack.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int insertFluid(Level world, BlockPos pos, FluidStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            IFluidHandler fluidHandler = world.getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.UP);
            if (fluidHandler != null) {
                int filledAmount = fluidHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                return filledAmount;
            }
        }
        return 0;
    }
}