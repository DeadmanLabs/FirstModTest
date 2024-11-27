package com.quantum.quantum_quarry.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import com.quantum.quantum_quarry.block.QuarryBlock;
import com.quantum.quantum_quarry.block.MinerBlock;
import com.quantum.quantum_quarry.QuantumQuarry;

public class ModBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(QuantumQuarry.MODID);
    public static final DeferredBlock<QuarryBlock> QUARRY = REGISTRY.register(
        "quarry", 
        () -> new QuarryBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    );
    public static final DeferredBlock<MinerBlock> MINER = REGISTRY.register(
        "miner", 
        () -> new MinerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    );
}