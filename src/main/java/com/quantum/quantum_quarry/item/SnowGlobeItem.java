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
    private static final String VISITED_BIOMES_TAG = "VisitedBiomes";
    private static final int REQUIRED_BIOMES = 7;

    public SnowGlobeItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CompoundTag tag = stack.get();
        if (this.world != null) {
            Set<ResourceLocation> allBiomes = world.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).keySet();
            if (tag != null && tag.contains(VISITED_BIOMES_TAG)) {
                Set<String> visitedBiomes = new HashSet<>(tag.getList(VISITED_BIOMES_TAG, 8).stream().map(nbt -> nbt.getAsString()).toList());
                int biomesLeft = REQUIRED_BIOMES - visitedBiomes.size();

                tooltip.add(Component.literal("Biomes left to visit: " + biomesLeft));
                tooltip.add(Component.literal("Visited Biomes: " + String.join(", ", visitedBiomes)));
                List<String> remainingBiomes = new ArrayList<>();
                for (ResourceLocation biome : allBiomes) {
                    if (!visitedBiomes.contains(biome.toString())) {
                        remainingBiomes.add(biome.toString());
                    }
                }
                tooltip.add(Component.literal("Remaining Biomes: " + String.join(", ", remainingBiomes)));
            } else {
                tooltip.add(Component.literal("Biomes left to visit: " + REQUIRED_BIOMES));
            }
        }
    }

    public void checkBiomeVisit(Player player, ItemStack stack) {
        Level world = player.level();
        BlockPos pos = player.blockPosition();
        Biome biome = world.getBiome(pos).value();
        ResourceLocation biomeKey = world.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).getKey(biome);
        CompoundTag tag = stack.getOrCreateTag();
        ListTag visitedBiomesTag = tag.getList(VISITED_BIOMES_TAG, 8);
        Set<String> visitedBiomes = new HashSet<>();
        for (int i = 0; i < visitedBiomesTag.size(); i++) {
            visitedBiomes.add(visitedBiomesTag.getString(i));
        }
        if (visitedBiomes.contains(biomeKey.toString()) == false) {
            visitedBiomes.add(biomeKey.toString());
            visitedBiomesTag.add(StringTag.valueOf(biomeKey.toString()));
            List<String> biomeList = new ArrayList<>(visitedBiomes);
            tag.put(VISITED_BIOMES_TAG, visitedBiomesTag);
            if (biomeList.size() >= REQUIRED_BIOMES) {
                ItemStack magicStack = new ItemStack(MAGIC_SNOW_GLOBE.get(), 1);
                int slot = player.getInventory().findSlotMatchingItem(stack);
                if (slot != -1) {
                    player.getInventory().setItem(slot, magicStack);
                }
            }
        }
    }
}