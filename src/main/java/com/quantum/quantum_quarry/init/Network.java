package com.quantum.quantum_quarry.init;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.fml.common.EventBusSubscriber;

import com.quantum.quantum_quarry.packets.ChangeModeRequest;
import com.quantum.quantum_quarry.packets.SyncVisitedBiomesPayload;
import com.quantum.quantum_quarry.item.SnowGlobeItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;

@EventBusSubscriber(modid = QuantumQuarry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Network {
    private static final Logger LOGGER = LoggerFactory.getLogger(Network.class);
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("quantum_quarry").versioned("1");
        registrar.playToClient(
            SyncVisitedBiomesPayload.TYPE, 
            SyncVisitedBiomesPayload.STREAM_CODEC, 
            (payload, context) -> {
                Player player = context.player();
                if (player != null) {
                    LOGGER.info("Visited Biomes Sync Packet!");
                }
            }
        );
        final PayloadRegistrar networkThreadRegistrar = event.registrar("quantum_quarry").versioned("1").executesOn(HandlerThread.NETWORK);
        networkThreadRegistrar.playToServer(
            ChangeModeRequest.TYPE, 
            ChangeModeRequest.STREAM_CODEC, 
            (payload, context) -> {
                Player player = context.player();
                if (player != null) {
                    LOGGER.info("Quarry Mode Cycle Packet!");
                    BlockEntity blockEntity = player.level().getBlockEntity(payload.pos());
                    if (blockEntity instanceof QuarryBlockEntity quarryEntity) {
                        quarryEntity.cycleMode();
                    }
                }
            }
        );
    }
}
