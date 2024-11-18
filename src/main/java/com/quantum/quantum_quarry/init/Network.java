package com.quantum.quantum_quarry.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.events.network.SimpleNetworkManager;

public class Network {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        registrar.playBidirectional(
            ChangeModePacket.TYPE,
            ChangeModePacket.STREAM_CODEC,
            new DirectionalPayloadHandler<>(
                ClientPayloadHandler::handleDataOnMain,
                ServerPayloadHandler::handleDataOnMain
            )
        );
    }
}
