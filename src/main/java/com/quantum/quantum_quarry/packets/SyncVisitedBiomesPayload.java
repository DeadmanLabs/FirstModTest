package com.quantum.quantum_quarry.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;

public record SyncVisitedBiomesPayload(Set<ResourceLocation> visitedBiomes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncVisitedBiomesPayload> TYPE = new CustomPacketPayload.Type<SyncVisitedBiomesPayload>(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "sync_visited_viomes"));

    public static final StreamCodec<ByteBuf, SyncVisitedBiomesPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(
            HashSet::new, 
            ResourceLocation.STREAM_CODEC
        ), 
        SyncVisitedBiomesPayload::visitedBiomes, 
        SyncVisitedBiomesPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
