package com.quantum.quantum_quarry.helpers.ItemModels;

import java.util.function.Function;

import com.quantum.quantum_quarry.init.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

public class BiomeGlobeModelGeometry implements IUnbakedGeometry<BiomeGlobeModelGeometry> {
    public BiomeGlobeModelGeometry() {}

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        //BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("qunatum_quarry", "snow_globe"));
        return new BiomeGlobeModel(null, context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), spriteGetter.apply(context.getMaterial("particle")), overrides);
    }
}
