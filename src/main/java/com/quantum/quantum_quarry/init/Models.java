package com.quantum.quantum_quarry.init;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.helpers.ItemModels.BiomeGlobeModelLoader;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Models {
    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(BiomeGlobeModelLoader.ID, BiomeGlobeModelLoader.INSTANCE);
    }
}
