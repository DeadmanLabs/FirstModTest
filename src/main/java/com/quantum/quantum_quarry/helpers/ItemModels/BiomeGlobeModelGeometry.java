package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;

import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.function.Function;

import com.quantum.quantum_quarry.helpers.ItemModels.BiomeGlobeModel;

public class BiomeGlobeModelGeometry implements IUnbakedGeometry<BiomeGlobeModelGeometry> {
    public BiomeGlobeModelGeometry() {}

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return new BiomeGlobeModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), spriteGetter.apply(context.getMaterial("particle")), overrides);
    }
}
