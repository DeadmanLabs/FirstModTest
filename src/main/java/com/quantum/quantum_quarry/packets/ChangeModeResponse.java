package com.quantum.quantum_quarry.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.codec.StreamDecoder;

import io.netty.buffer.ByteBuf;

public record ChangeModeResponse(BlockPos pos, int mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeModeResponse> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "change_packet_response"));

    public static final StreamCodec<ByteBuf, ChangeModeResponse> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ChangeModeResponse::pos,
        ByteBufCodecs.INT,
        ChangeModeResponse::mode,
        ChangeModeResponse::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}