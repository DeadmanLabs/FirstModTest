package com.quantum.quantum_quarry.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

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
}