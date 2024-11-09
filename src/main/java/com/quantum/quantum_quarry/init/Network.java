package com.quantum.quantum_quarry.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import com.quantum.quantum_quarry.packets.SyncVisitedBiomesPayload;

public class Network {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
            SyncVisitedBiomesPayload.TYPE,
            SyncVisitedBiomesPayload.STREAM_CODEC, 
            (payload, context) -> {
                
            });
    }
}
