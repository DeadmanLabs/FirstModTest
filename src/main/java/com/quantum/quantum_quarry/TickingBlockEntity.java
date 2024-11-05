package com.quantum.quantum_quarry;
import javax.annotation.Nullable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.quantum_quarry.helpers.ChunkMiner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class TickingBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(TickingBlockEntity.class);
    private int tickCount = 0;
    private UUID placingPlayerUUID;
    public ChunkMiner manager;
    public static final int TANK_CAPACITY = 20000;
    private final EnergyStorage energyStorage = new EnergyStorage(200000);
    private Queue<FluidStack> fluidStorage = new LinkedList<>();
    private BlockPos location;

    public TickingBlockEntity(BlockPos pos, BlockState state) {
        super(QuantumQuarry.TICKING_BLOCK_ENTITY.get(), pos, state);
        this.location = pos;
        LOGGER.info("TickingBlockEntity created at position: {}", pos);
    }

    public void setPlacingPlayerUUID(UUID uuid) {
        this.placingPlayerUUID = uuid;
        if (this.level instanceof ServerLevel serverLevel) {
            this.manager = new ChunkMiner(serverLevel);
        } else {
            LOGGER.info("Set owner but could not get instance of ServerLevel");
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TickingBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.tickCount++;
            //LOGGER.info("Tick count at position {}: {}", pos, blockEntity.tickCount);
                if (blockEntity.placingPlayerUUID != null && level instanceof ServerLevel serverLevel && blockEntity.manager != null) {
                    Player player = serverLevel.getPlayerByUUID(blockEntity.placingPlayerUUID);
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
                                //normally we would handle the fluid add failure here, but I can just burn it cause I dont care
                            }
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

    // We use the below functions to keep block info after its been broken. Very useful for blocks that have an owner
    @Override
    protected void saveAdditional(CompoundTag tag, Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("TickCount", this.tickCount);
        if (this.placingPlayerUUID != null) {
            tag.putUUID("PlacingPlayerUUID", this.placingPlayerUUID);
        }
        tag.put("Energy", energyStorage.serializeNBT(provider));
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
        this.tickCount = tag.getInt("TickCount");
        if (tag.hasUUID("PlacingPlayerUUID")) {
            this.placingPlayerUUID = tag.getUUID("PlacingPlayerUUID");
        }
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(provider, tag.get("Energy"));
        }
        fluidStorage.clear();
        ListTag fluidList = tag.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < fluidList.size(); i++) {
            CompoundTag fluidTag = fluidList.getCompound(i);
            FluidStack fluid = FluidStack.parseOptional(provider, fluidTag);
            fluidStorage.add(fluid);
        }
    }
}