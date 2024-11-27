package com.quantum.quantum_quarry.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.common.MutableDataComponentHolder;

import java.util.List;

import com.quantum.quantum_quarry.init.DataComponents;

public class BiomeMarkerItem extends Item {
    public BiomeMarkerItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)  {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            BlockPos playerPos = player.blockPosition();
            Holder<Biome> biomeHolder = world.getBiome(playerPos);
            ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);

            if (biomeKey != null) {
                ((MutableDataComponentHolder)itemStack).set(DataComponents.STORED_BIOME.get(), biomeKey);
                player.displayClientMessage(Component.literal("Stored Biome: " + biomeKey.location()), true);
            } else {
                player.displayClientMessage(Component.literal("Failed to indentify the current biome."), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ResourceKey<Biome> storedBiome = ((MutableDataComponentHolder)stack).get(DataComponents.STORED_BIOME.get());
        if (storedBiome != null) {
            tooltip.add(Component.literal("Stored Biome: " + storedBiome.location()));
        } else {
            tooltip.add(Component.literal("Stored Biome: None"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        ResourceKey<Biome> storedBiome = stack.get(DataComponents.STORED_BIOME.get());
        return storedBiome != null;
    }
}