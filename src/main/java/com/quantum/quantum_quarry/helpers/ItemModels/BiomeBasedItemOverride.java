package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;

import java.util.List;
import java.util.function.Function;

import net.neoforged.neoforge.client.model.IDynamicBakedModel;

public class BiomeBasedItemOverride extends ItemOverrides {
    public BiomeBasedItemOverride(ModelBaker modelBaker, UnbakedModel unbakedModel, List<ItemOverride> overrides, Function<Material, TextureAtlasSprite> spriteGetter) {
        super(modelBaker, unbakedModel, overrides, spriteGetter);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        if (level == null || entity == null) {
            return originalModel;
        }

        BlockPos pos = entity.blockPosition();
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeName = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);

        BakedModel biomeModel = generateBiomeSpecificModel(biomeName);

        return biomeModel != null ? biomeModel : originalModel;
    }

    private BakedModel generateBiomeSpecificModel(ResourceLocation biomeName) {
        ModelResourceLocation modelLocation = new ModelResourceLocation(
            ResourceLocation.fromNamespaceAndPath("quantum_quarry", "item/temp"), 
            "inventory"
        );
        return Minecraft.getInstance().getModelManager().getModel(modelLocation);
    }
}
