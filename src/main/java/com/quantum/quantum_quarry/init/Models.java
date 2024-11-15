package com.quantum.quantum_quarry.init;

import net.minecraft.world.item.Item;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class Models {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Item ourItem = ModItems.SNOW_GLOBE.get();
            
        });
    }
}
