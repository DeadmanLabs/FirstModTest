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
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BiomeMarkerItem extends Item {
    private static final String BIOME_TAG_KEY = "Empty";
    public BiomeMarkerItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            BlockPos playerPos = player.blockPosition();
            Biome biome = world.getBiome(playerPos).value();
            CompoundTag nbt = itemstack.getOrCreateTag();
            ResourceLocation biomeName = world.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
            nbt.putString(BIOME_TAG_KEY, biomeName.toString());
            return InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);

        CompoundTag nbt = itemstack.getTag();
        if (nbt != null && nbt.contains(BIOME_TAG_KEY)) {
            String biomeName = nbt.getString(BIOME_TAG_KEY);
            list.add(Component.literal("Stored Biome: " + biomeName));
        } else {
            list.add(Component.literal("Stored Biome: Empty"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(BIOME_TAG_KEY);
    }
}