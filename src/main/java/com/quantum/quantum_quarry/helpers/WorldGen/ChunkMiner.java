package com.quantum.quantum_quarry.helpers.WorldGen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.List;

import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;
import com.quantum.quantum_quarry.helpers.WorldGen.TemporaryWorldUtil;

public class ChunkMiner {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarryBlockEntity.class);
    private final ServerLevel level;
    private final Holder<Biome> biome;
    public final Queue<ItemStack> itemsToGive = new LinkedList<>();
    public final Queue<FluidStack> fluidsToGive = new LinkedList<>();

    public ChunkMiner(ServerLevel level, Holder<Biome> biome) {
        this.level = level;
        this.biome = biome;
    }

    public void startMining() {
        LOGGER.info("Generating chunk...");
        ProtoChunk chunk = TemporaryWorldUtil.createVoidChunk(this.level, biome);
        LOGGER.info("Chunk Generated!");
        if (chunk != null) {
            LOGGER.info("Mining Chunk...");
            simulateMining(chunk);
            LOGGER.info("Mined Chunk!");
        } else {
            LOGGER.info("Failed to generate chunk!");
        }
    }

    private void simulateMining(ProtoChunk chunk, int fortune) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                }
                else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                        if (fortune > 0) {
                            List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, null, getFortuneTool(fortune));
                            itemsToGive.addAll(drops);
                        }
                }
            }
        }
    }

    private void simulateSilktouchMining(ProtoChunk chunk) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), level.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), level.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                }
                else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity, null, getSilkTool());
                    itemsToGive.addAll(drops);
                }
            }
        }
    }

    private void simulateMining(ProtoChunk chunk) {
        for (BlockPos pos : BlockPos.betweenClosed(chunk.getPos().getMinBlockX(), chunk.getMinBuildHeight(), chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), chunk.getMaxBuildHeight() - 1, chunk.getPos().getMaxBlockZ())) {
            BlockState state = chunk.getBlockState(pos);
            Block block = state.getBlock();
            if (block != Blocks.AIR) {
                FluidState fluidState = state.getFluidState();
                if (!fluidState.isEmpty() && fluidState.isSource()) {
                    FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
                    fluidsToGive.add(fluidStack);
                } else {
                    BlockEntity blockEntity = chunk.getBlockEntity(pos);
                    List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity);
                    itemsToGive.addAll(drops);
                }
            }
        }
    }
    
    private ItemStack getFortuneTool(int fortune) {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        ResourceKey<Enchantment> fortuneKey = Enchantments.FORTUNE;
        Registry<Enchantment> enchantmentRegistry = this.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> fortuneHolder = enchantmentRegistry.getHolder(fortuneKey);
        fortuneHolder.ifPresent(holder -> tool.enchant(holder, fortune));
        return tool;
    }

    private ItemStack getSilkTool() {
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        ResourceKey<Enchantment> silktouchKey = Enchantments.SILK_TOUCH;
        Registry<Enchantment> enchantmentRegistry = this.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> silktouchHolder = enchantmentRegistry.getHolder(silktouchKey);
        silktouchHolder.ifPresent(holder -> tool.enchant(holder, 1));
        return tool;
    }
}
