package com.quantum.quantum_quarry.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.quantum.quantum_quarry.item.BiomeMarkerItem;
import com.quantum.quantum_quarry.item.SnowGlobeItem;
import com.quantum.quantum_quarry.item.MagicSnowGlobeItem;
import com.quantum.quantum_quarry.QuantumQuarry;

public class ModItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(QuantumQuarry.MODID);
    public static final DeferredItem<BlockItem> QUARRY = REGISTRY.register(
        "quarry",
        () -> new BlockItem(ModBlocks.QUARRY.get(), new Item.Properties())
    );
    public static final DeferredItem<BlockItem> MINER = REGISTRY.register(
        "miner",
        () -> new BlockItem(ModBlocks.MINER.get(), new Item.Properties())
    );
    public static final DeferredItem<MagicSnowGlobeItem> MAGIC_SNOW_GLOBE = REGISTRY.register(
        "magic_snow_globe", 
        MagicSnowGlobeItem::new
    );
    public static final DeferredItem<SnowGlobeItem> SNOW_GLOBE = REGISTRY.register(
        "snow_globe", 
        SnowGlobeItem::new
    );
    public static final DeferredItem<BiomeMarkerItem> BIOME_MARKER = REGISTRY.register(
        "biome_marker",
        BiomeMarkerItem::new
    );
}
