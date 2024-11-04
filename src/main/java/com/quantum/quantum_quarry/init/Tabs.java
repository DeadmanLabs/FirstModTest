package com.quantum.quantum_quarry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class Tabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuantumQuarryMod.MODID);
    public static final RegistryObject<CreativeModeTab> QUANTUM_MINER = REGISTRY.register("quantum_miner",
                    () -> CreativeModeTab.builder().title(Component.translatable("item_group.quantum_quarry.quantum_miner")).icon(() -> new ItemStack(Blocks.MINER.get())).displayItems((parameters, tabData) -> {
                        tabData.accept(Blocks.QUARRY.get().asItem());
                        tabData.accept(Blocks.MINER.get().asItem());
                        tabData.accept(Items.MAGIC_SNOW_GLOBE.get());
                        tabData.accept(Items.SNOW_GLOBE.get());
                        tabData.accept(Items.BIOME_MARKER.get());
                    }).withSearchBar().build());
}