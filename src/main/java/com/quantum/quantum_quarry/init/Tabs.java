package com.quantum.quantum_quarry.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.quantum.quantum_quarry.QuantumQuarry;

public class Tabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuantumQuarry.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUANTUM_MINER = REGISTRY.register(
        "quantum_miner",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.quantum_quarry.quantum_miner"))
            .icon(() -> new ItemStack(ModBlocks.MINER.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.QUARRY.get().asItem());
                output.accept(ModBlocks.MINER.get().asItem());
                output.accept(ModItems.MAGIC_SNOW_GLOBE.get());
                output.accept(ModItems.SNOW_GLOBE.get());
                output.accept(ModItems.BIOME_MARKER.get());
            })
            .withSearchBar()
            .build()
    );
}