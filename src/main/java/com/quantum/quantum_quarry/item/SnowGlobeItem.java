package com.quantum.quantum_quarry.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;

import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.quantum.quantum_quarry.init.DataComponents;
import com.quantum.quantum_quarry.init.ModItems;
import com.quantum.quantum_quarry.packets.SyncVisitedBiomesPayload;
import com.quantum.quantum_quarry.item.data.VisitedBiomes;

public class SnowGlobeItem extends Item {
    private static final int REQUIRED_BIOMES = 7;

    public SnowGlobeItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && entity instanceof Player player) {
            checkBiomeVisits(player, stack);
        }
    }

    private void checkBiomeVisits(Player player, ItemStack stack) {
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        Biome biome = world.getBiome(pos).value();
        ResourceKey<Biome> biomeKey = world.registryAccess().registryOrThrow(Registries.BIOME).getResourceKey(biome).orElse(null);
        VisitedBiomes visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (biomeKey == null) {
            return;
        }
        if (visitedBiomes == null || visitedBiomes.biomes().isEmpty()) {
            VisitedBiomes newVisitedBiomes = new VisitedBiomes(UUID.randomUUID(), new HashSet<>());
            visitedBiomes = stack.set(DataComponents.VISITED_BIOMES.get(), newVisitedBiomes);
        }
        if (visitedBiomes != null && !visitedBiomes.biomes().contains(biomeKey)) {
            visitedBiomes.biomes().add(biomeKey);
            stack.set(DataComponents.VISITED_BIOMES.get(), visitedBiomes);
            player.displayClientMessage(Component.literal("Visited new biome: " + biomeKey.location() + " / " + visitedBiomes.biomes().size()), true);
            HashSet<ResourceLocation> resourceLocations = new HashSet<>();
            for (ResourceKey<Biome> biomeResourceKey : visitedBiomes.biomes()) {
                resourceLocations.add(biomeResourceKey.location());
            }
            if (player instanceof ServerPlayer serverPlayer) {
                /*
                SyncVisitedBiomesPayload pkt = new SyncVisitedBiomesPayload(new HashSet<ResourceLocation>(resourceLocations));
                PacketDistributor.sendToPlayer(serverPlayer, pkt);
                */
                sendSyncPacket(serverPlayer, visitedBiomes);
            }
            if (visitedBiomes.biomes().size() >= REQUIRED_BIOMES) {
                ItemStack magicSnowGlobe = new ItemStack(ModItems.MAGIC_SNOW_GLOBE.get());
                player.getInventory().removeItem(stack);
                player.getInventory().add(magicSnowGlobe);
            }
        }
    }

    private void checkBiomeVisits(ServerPlayer player, ItemStack stack) {
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        Biome biome = world.getBiome(pos).value();
        ResourceKey<Biome> biomeKey = world.registryAccess().registryOrThrow(Registries.BIOME).getResourceKey(biome).orElse(null);
        if (biomeKey == null) {
            return;
        }
        VisitedBiomes visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (visitedBiomes == null) {
            VisitedBiomes newVisitedBiomes = new VisitedBiomes(UUID.randomUUID(), new HashSet<>());
            visitedBiomes = stack.set(DataComponents.VISITED_BIOMES.get(), newVisitedBiomes);
        }
        if (visitedBiomes != null && !visitedBiomes.biomes().contains(biomeKey)) {
            visitedBiomes.biomes().add(biomeKey);
            stack.set(DataComponents.VISITED_BIOMES.get(), visitedBiomes);
            player.displayClientMessage(Component.literal("Visited new biome: " + biomeKey.location() + " / " + visitedBiomes.biomes().size()), true);
            if (visitedBiomes.biomes().size() >= REQUIRED_BIOMES) {
                ItemStack magicSnowGlobe = new ItemStack(ModItems.MAGIC_SNOW_GLOBE.get());
                player.getInventory().removeItem(stack);
                player.getInventory().add(magicSnowGlobe);
            }
            sendSyncPacket(player, visitedBiomes);
        }
    }

    private void sendSyncPacket(ServerPlayer player, VisitedBiomes visitedBiomes) {
        Set<ResourceLocation> biomeLocations = visitedBiomes.biomes().stream()
            .map(ResourceKey::location)
            .collect(Collectors.toSet());
        SyncVisitedBiomesPayload payload = new SyncVisitedBiomesPayload(visitedBiomes.id(), biomeLocations);
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        VisitedBiomes visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (visitedBiomes != null) {
            tooltip.add(Component.literal("Visited Biomes (" + visitedBiomes.biomes().size() + "):"));
            for (ResourceKey<Biome> biome : visitedBiomes.biomes()) {
                tooltip.add(Component.literal("- " + biome.location()));
            }
        } else {
            tooltip.add(Component.literal("No biomes visited yet. (" + visitedBiomes + ")"));
        }
    }
}