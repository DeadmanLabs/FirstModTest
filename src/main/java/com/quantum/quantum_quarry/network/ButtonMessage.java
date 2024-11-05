package com.quantum.quantum_quarry.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import com.quantum.quantum_quarry.world.inventory.QuantumMinerScreenMenu;
import com.quantum.quantum_quarry.QuantumQuarryMod;
import com.quantum.quantum_quarry.block.entity.MinerBlockEntity;

import java.util.function.Supplier;
import java.util.HashMap;

public class ButtonMessage {
    private final int buttonID, x, y, z;

    public ButtonMessage(FriendlyByteBuf buffer) {
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public ButtonMessage(int buttonID, int x, int y, int z) {
        this.buttonID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public static void buffer(ButtonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.buttonID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public static void handler(ButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player entity = context.getSender();
            int buttonID = message.buttonID;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            handleButtonAction(entity, buttonID x, y, z);
        });
        context.setPacketHandled(true);
    }
}