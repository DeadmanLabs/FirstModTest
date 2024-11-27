package com.quantum.quantum_quarry.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

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
import com.quantum.quantum_quarry.packets.ChangeModeResponse;
import com.quantum.quantum_quarry.item.SnowGlobeItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.item.data.VisitedBiomes;

import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

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
                    UUID itemId = payload.id();
                    Set<ResourceKey<Biome>> updatedBiomes = payload.visitedBiomes().stream()
                        .map(location -> ResourceKey.create(Registries.BIOME, location))
                        .collect(Collectors.toSet());
                    for (ItemStack stack : player.getInventory().items) {
                        if (stack.getItem() instanceof SnowGlobeItem) {
                            VisitedBiomes visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
                            if (visitedBiomes != null && visitedBiomes.id().equals(itemId)) {
                                VisitedBiomes updatedVisitedBiomes = new VisitedBiomes(itemId, updatedBiomes);
                                stack.set(DataComponents.VISITED_BIOMES.get(), updatedVisitedBiomes);
                                break;
                            }
                        }
                    }
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
                    context.enqueueWork(() -> {
                        BlockPos pos = payload.pos();
                        Level world = player.level();

                        if (world.isLoaded(pos)) {
                            BlockEntity blockEntity = world.getBlockEntity(pos);
                            if (blockEntity instanceof QuarryBlockEntity quarryEntity) {
                                quarryEntity.cycleMode();
                                if (player instanceof ServerPlayer serverPlayer) {
                                    ChangeModeResponse pkt = new ChangeModeResponse(pos, quarryEntity.mode);
                                    serverPlayer.connection.send(new ClientboundCustomPayloadPacket(pkt));
                                }
                            }
                        }
                    });
                }
            }
        );
        networkThreadRegistrar.playToClient(
            ChangeModeResponse.TYPE,
            ChangeModeResponse.STREAM_CODEC,
            (payload, context) -> {
                Player player = context.player();
                if (player != null) {
                    BlockEntity blockEntity = player.level().getBlockEntity(payload.pos());
                    if (blockEntity instanceof QuarryBlockEntity quarryEntity) {
                        quarryEntity.mode = payload.mode();
                    }
                }
            }
        );
    }
}
