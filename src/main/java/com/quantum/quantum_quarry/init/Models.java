package com.quantum.quantum_quarry.init;

import net.minecraft.world.item.Item;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class Models {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Item ourItem = ModItems.SNOW_GLOBE.get();
            ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ourItem.getReigsitryName(), "inventory");
            BakedModel existingModel = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);

            if (existingModel != null) {
                BakedModel customModel = new BiomeBasedItemModel(existingModel, modelBaker, unbakedModel, overridesList, spriteGetter);
                Minecraft.getInstance().getModelManager().getModelRegistry().put(modelResourceLocation, customModel);
            }
        });
    }
}
