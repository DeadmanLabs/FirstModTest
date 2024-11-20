package com.quantum.quantum_quarry.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.init.BlockEntities;
import com.quantum.quantum_quarry.procedures.FindCore;
import com.quantum.quantum_quarry.block.MinerBlock;
import com.quantum.quantum_quarry.block.entity.MinerBlockEntity;
import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = QuantumQuarry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BlockCapabilities {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlockCapabilities.class);
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            BlockEntities.QUARRY_BLOCK_ENTITY.get(),
            (quarryEntity, direction) -> {
                return quarryEntity.getEnergyStorage();
            }
        );
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            BlockEntities.MINER_BLOCK_ENTITY.get(),
            (minerEntity, direction) -> {
                BlockPos minerPos = minerEntity.getBlockPos();
                BlockPos quarryPos = FindCore.execute(minerEntity.getLevel(), minerPos.getX(), minerPos.getY(), minerPos.getZ());
                if (quarryPos != null) {
                    BlockEntity blockEntity = minerEntity.getLevel().getBlockEntity(quarryPos);
                    if (blockEntity != null && blockEntity instanceof QuarryBlockEntity quarryEntity) {
                        return quarryEntity.getEnergyStorage();
                    }
                }
                return null;
            }
        );
    }
}