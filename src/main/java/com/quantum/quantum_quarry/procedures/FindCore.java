package com.quantum.quantum_quarry.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

public class FindCore {
    public static double[] execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null)
            return new double[3];
        double rootX = 0;
        double rootY = 0;
        double rootZ = 0;
        boolean rootFound = true;
        BlockPos[] positions = {
            BlockPos.containing(x + 1, y, z),
            BlockPos.containing(x - 1, y, z),
            BlockPos.containing(x, y + 1, z),
            BlockPos.containing(x, y - 1, z),
            BlockPos.containing(x, y, z + 1),
            BlockPos.containing(x, y, z - 1)
        };
        for (BlockPos pos : positions) {
            if (world.getBlockState(pos).getBlock() == Blocks.QUARRY.get()) {
                rootX = pos.x;
                rootY = pos.y;
                rootZ = pos.z;
                rootFound = true;
            }
        }
        if (rootFound) {
            return new double[] { rootX, rootY, rootZ };
        } else {
            return new double[3];
        }
    }

    public static boolean validateStructure(LevelAccessor world, double rootX, double rootY, double rootZ, Entity entity) {
        BlockPos[] positions = {
            BlockPos.containing(rootX + 1, rootY, rootZ),
            BlockPos.containing(rootX - 1, rootY, rootZ),
            BlockPos.containing(rootX, rootY + 1, rootZ),
            BlockPos.containing(rootX, rootY - 1, rootZ),
            BlockPos.containing(rootX, rootY, rootZ + 1),
            BlockPos.containing(rootX, rootY, rootZ - 1)
        };
        boolean valid = true;
        for (BlockPos pos : positions) {
            valid = valid && (world.getBlockState(pos).getBlock() == Blocks.MINER.get());
        }
        return valid;
    }
}