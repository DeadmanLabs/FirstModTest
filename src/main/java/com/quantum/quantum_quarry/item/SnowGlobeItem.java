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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SnowGlobeItem extends Item {
    private static final int REQUIRED_BIOMES = 7;

    public SnowGlobeItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        
        Set<ResourceLocation> visitedBiomes = stack.get(GlobeDataComponents.VISITED_BIOMES).orElse(Collections.emptySet());
        int biomesLeft = REQUIRED_BIOMES - visitedBiomes.size();

        tooltip.add(Component.literal("Biomes left to visit: " + biomesLeft));
        if (!visitedBiomes.isEmpty()) {
            tooltip.add(Component.literal("Visited Biomes:"));
            visitedBiomes.forEach(biome -> tooltip.add(Component.literal("- " + biome.toString())));
        }
    }

    public void checkBiomeVisit(Player player, ItemStack stack) {
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        Biome biome = world.getBiome(pos).value();
        ResourceLocation biomeKey = world.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);

        stack.update(GlobeDataComponents.VISITED_BIOMES, new HashSet<>(), visitedBiomes -> {
            if (visitedBiomes.add(biomeKey)) {
                player.displayClientMessage(Component.literal("Discovered new biome: " + biomeKey), true);
            }
            return visitedBiomes;
        });

        if (stack.get(GlobeDataComponents.VISITED_BIOMES).orElse(Collections.emptySet()).size() >= REQUIRED_BIOMES) {
            ItemStack magicStack = new ItemStack(ModItems.MAGIC_SNOW_GLOBE.get());
            player.getInventory().removeItem(stack);
            player.getInventory().add(magicStack);
            player.displayClientMessage(Component.literal("Snow Globe upgraded to Magic Snow Globe!"), true);
        }
    }
}