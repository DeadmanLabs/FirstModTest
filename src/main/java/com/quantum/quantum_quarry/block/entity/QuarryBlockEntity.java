package com.quantum.quantum_quarry.block.entity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;

import com.quantum.quantum_quarry.ChunkMiner;
import com.quantum.quantum_quarry.QuantumQuarry;

public class QuarryBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarryBlockEntity.class);
    private UUID owner;
    public ChunkMiner manager;
    public static final int TANK_CAPACITY = 20000;
    private final EnergyStorage energyStorage = new EnergyStorage(200000);
    private Queue<FluidStack> fluidStorage = new LinkedList<>();
    private BlockPos location;

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(QuantumQuarry.QUARRY_BLOCK_ENTITY.get(), pos, state);
        this.location = pos;
        LOGGER.info("Quantum Miner created at position: {}", pos);    
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
        if (this.level instanceof ServerLevel serverLevel) {
            this.manager = new ChunkMiner(serverLevel);
        } else {
            LOGGER.info("Set owner but could not get instance of ServerLevel");
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, QuarryBlockEntity blockEntity) {
        if (!level.isClientSide && blockEntity.owner != null && level instanceof ServerLevel serverLevel && blockEntity.manager != null) {
            Player player = serverLevel.getPlayerByUUID(blockEntity.owner);
            if (player != null) {
                if (blockEntity.manager.itemsToGive.size() <= 0) {
                    blockEntity.manager.startMining();
                }
                ItemStack item = blockEntity.manager.itemsToGive.poll();
                FluidStack fluid = blockEntity.manager.fluidsToGive.poll();
                if (item != null && blockEntity.energyStorage.extractEnergy(1, true) == 1) {
                    blockEntity.energyStorage.extractEnergy(1, false);
                    player.getInventory().add(item);
                    if (fluid != null) {
                        boolean added = blockEntity.addFluidToStorage(fluid);
                    }
                }
            }
        }
    }

    private int getTotalFluidVolume() {
        return fluidStorage.stream().mapToInt(FluidStack::getAmount).sum();
    }

    private boolean addFluidToStorage(FluidStack fluid) {
        int currentVolume = getTotalFluidVolume();
        if (currentVolume + fluid.getAmount() <= TANK_CAPACITY) {
            fluidStorage.add(fluid);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        level.invalidateCapabilities(this.location);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Energy", energyStorage.serializeNBT(provider));
        tag.putUUID("Owner", this.owner);
        ListTag fluidList = new ListTag();
        for (FluidStack fluid : fluidStorage) {
            CompoundTag fluidTag = new CompoundTag();
            fluid.save(provider, fluidTag);
            fluidList.add(fluidTag);
        }
        tag.put("Fluids", fluidList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.get("Energy") instanceof IntTag energyTag) {
            energyStorage.deserializeNBT(provider, energyTag);
        }
        if (tag.hasUUID("Owner")) {
            this.owner = tag.getUUID("Owner");
        }
        if (tag.getList("Fluids", Tag.TAG_COMPOUND) instanceof ListTag fluidsTag) {
            for (int i = 0; i < fluidsTag.size(); i++) {
                CompoundTag fluidTag = fluidsTag.getCompound(i);
                FluidStack fluid = FluidStack.parseOptional(provider, fluidTag);
                fluidStorage.add(fluid);
            }
        }
    }
}