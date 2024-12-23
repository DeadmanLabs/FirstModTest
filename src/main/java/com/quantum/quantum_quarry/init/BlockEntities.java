package com.quantum.quantum_quarry.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.block.entity.MinerBlockEntity;
import com.quantum.quantum_quarry.QuantumQuarry;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, QuantumQuarry.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuarryBlockEntity>> QUARRY_BLOCK_ENTITY = REGISTRY.register(
        "quarry_block_entity",
        () -> BlockEntityType.Builder.of(QuarryBlockEntity::new, ModBlocks.QUARRY.get()).build(null)
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MinerBlockEntity>> MINER_BLOCK_ENTITY = REGISTRY.register(
        "miner_block_entity",
        () -> BlockEntityType.Builder.of(MinerBlockEntity::new, ModBlocks.MINER.get()).build(null)
    );
}
