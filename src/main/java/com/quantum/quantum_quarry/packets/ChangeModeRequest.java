package com.quantum.quantum_quarry.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record ChangeModeRequest(BlockPos pos, int mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeModeRequest> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "change_packet_request"));

    public static final StreamCodec<ByteBuf, ChangeModeRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ChangeModeRequest::pos,
        ByteBufCodecs.BLOCK_POS,
        ChangeModeRequest::mode,
        ChangeModeRequest::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}