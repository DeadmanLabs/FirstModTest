package com.quantum.quantum_quarry;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class Tick {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        boolean rootFound = false;
        double rootX = 0;
        double rootY = 0;
        double rootZ = 0;
        double tickCount = 0;
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
            BlockPos[] positions = {
                BlockPos.containing(rootX + 1, rootY, rootZ),
                BlockPos.containing(rootX - 1, rootY, rootZ),
                BlockPos.containing(rootX, rootY + 1, rootZ),
                BlockPos.containing(rootX, rootY - 1, rootZ),
                BlockPos.containing(rootX, rootY, rootZ + 1),
                BlockPos.containing(rootX, rootY, rootZ - 1)
            };
            boolean built = true;
            for (BlockPos pos : positions) {
                built = built && (world.getBlockState(pos).getBlock() == Blocks.MINER.get());
            }
            if (built) {
                tickCount = tickCount + 1;
            }
        }
    }
}