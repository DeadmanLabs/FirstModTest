package com.quantum.quantum_quarry.helpers.ItemModels;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;

import com.quantum.quantum_quarry.helpers.ItemModels.BiomeGlobeModelGeometry;
public class BiomeGlobeModelLoader implements IGeometryLoader<BiomeGlobeModelGeometry> {
    public static final BiomeGlobeModelLoader INSTANCE = new BiomeGlobeModelLoader();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("quantum_quarry", "biome_globe_loader");
    private BiomeGlobeModelLoader() {}

    @Override
    public BiomeGlobeModelGeometry read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        return new BiomeGlobeModelGeometry();
    }
}
