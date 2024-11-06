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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.quantum.quantum_quarry.init.DataComponents;
import com.quantum.quantum_quarry.init.ModItems;

public class SnowGlobeItem extends Item {
    private static final int REQUIRED_BIOMES = 7;

    public SnowGlobeItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && entity instanceof Player player) {
            updateVisitedBiomes(stack, level, player);
        }
    }

    private void updateVisitedBiomes(ItemStack stack, Level level, Player player) {
        ResourceKey<Biome> currentBiome = level.getBiome(player.blockPosition()).unwrapKey().orElse(null);
        if (currentBiome == null) return;

        Set<ResourceKey<Biome>> visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (visitedBiomes == null) {
            visitedBiomes = new HashSet<>();
        }

        if (visitedBiomes.add(currentBiome)) {
            stack.set(DataComponents.VISITED_BIOMES.get(), visitedBiomes);
            player.displayClientMessage(Component.literal("Visited new biome: " + currentBiome.location()), true);
        }
    }

    private void checkBiomeVisits(Player player, ItemStack stack) {
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        Biome biome = world.getBiome(pos).value();
        ResourceKey<Biome> biomeKey = world.registryAccess().registryOrThrow(Registries.BIOME).getResourceKey(biome).orElse(null);
        if (biomeKey == null) {
            return;
        }
        Set<ResourceKey<Biome>> visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (visitedBiomes.isEmpty()) {
            visitedBiomes = stack.set(DataComponents.VISITED_BIOMES.get(), new HashSet<>());
        }
        if (!visitedBiomes.contains(biomeKey)) {
            visitedBiomes.add(biomeKey);
            stack.set(DataComponents.VISITED_BIOMES.get(), visitedBiomes);
            if (visitedBiomes.size() >= REQUIRED_BIOMES) {
                ItemStack magicSnowGlobe = new ItemStack(ModItems.MAGIC_SNOW_GLOBE.get());
                player.getInventory().removeItem(stack);
                player.getInventory().add(magicSnowGlobe);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        Set<ResourceKey<Biome>> visitedBiomes = stack.get(DataComponents.VISITED_BIOMES.get());
        if (visitedBiomes != null && !visitedBiomes.isEmpty()) {
            tooltip.add(Component.literal("Visited Biomes:"));
            for (ResourceKey<Biome> biome : visitedBiomes) {
                tooltip.add(Component.literal("- " + biome.location()));
            }
        } else {
            tooltip.add(Component.literal("No biomes visited yet."));
        }
    }
}