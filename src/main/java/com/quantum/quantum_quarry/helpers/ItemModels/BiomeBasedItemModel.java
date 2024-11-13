package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;

import net.neoforged.neoforge.client.model.BakedModelWrapper;

import com.quantum.quantum_quarry.helpers.ItemModels.BiomeBasedItemOverride;

public class BiomeBasedItemModel extends BakedModelWrapper<BakedModel> {
    private final ItemOverrides overrides;

    public BiomeBasedItemModel(BakedModel originalModel) {
        super(originalModel);
        this.overrides = new BiomeBaesdItemOverride();
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }
}
