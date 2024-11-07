package com.quantum.quantum_quarry.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.quantum.quantum_quarry.world.inventory.QuantumMinerScreenMenu;
import com.quantum.quantum_quarry.QuantumQuarryMod;
import com.quantum.quantum_quarry.block.entity.MinerBlockEntity;

import java.util.function.Supplier;
import java.util.HashMap;

public class ButtonMessage implements CustomPacketPayload {
    private final int buttonID, x, y, z;

    public ButtonMessage(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public ButtonMessage(int buttonID, int x, int y, int z) {
        this.buttonID = buttonID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void buffer(ButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public ResourceLocation getType() {
        return ResourceLocation.fromNamespaceAndPath("quantum_quarry", "button_message");
    }

    public static void handler(ButtonMessage message, IPayloadContext context) {
        if (context.player() != null && context.player() instanceof ServerPlayer serverPlayer) {
            int buttonID = message.buttonID;
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
        }
    }
}