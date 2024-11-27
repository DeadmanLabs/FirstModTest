package com.quantum.quantum_quarry.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.codec.StreamDecoder;

import io.netty.buffer.ByteBuf;

public record ChangeModeRequest(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeModeRequest> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "change_packet_request"));

    public static final StreamCodec<ByteBuf, ChangeModeRequest> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ChangeModeRequest::pos,
        ChangeModeRequest::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}