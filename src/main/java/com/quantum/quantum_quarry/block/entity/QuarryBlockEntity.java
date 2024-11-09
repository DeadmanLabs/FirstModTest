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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;

import com.quantum.quantum_quarry.helpers.ChunkMiner;
import com.quantum.quantum_quarry.init.BlockEntities;
import com.quantum.quantum_quarry.world.inventory.ScreenMenu;
import com.quantum.quantum_quarry.procedures.FindCore;

import io.netty.buffer.Unpooled;

public class QuarryBlockEntity extends BlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarryBlockEntity.class);
    private UUID owner;
    public ChunkMiner manager;
    public static final int TANK_CAPACITY = 20000;
    private final EnergyStorage energyStorage = new EnergyStorage(200000);
    private Queue<FluidStack> fluidStorage = new LinkedList<>();
    private BlockPos location;
    private int mode;

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.QUARRY_BLOCK_ENTITY.get(), pos, state);
        this.location = pos;
        this.mode = 0;
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
                BlockPos core = FindCore.execute(level, pos.getX(), pos.getY(), pos.getZ()); //ensure that we are using the core for validation
                if (FindCore.validateStructure(level, core)) { //We can generate the chunk without having a valid structure to save ticks
                    ItemStack item = blockEntity.manager.itemsToGive.poll();
                    FluidStack fluid = blockEntity.manager.fluidsToGive.poll();
                    if (item != null /* && blockEntity.energyStorage.extractEnergy(1, true) == 1 */) {
                        //blockEntity.energyStorage.extractEnergy(1, false);
                        BlockPos[] storages = FindCore.findStorage(level, core);
                        for (BlockPos storage : storages) {
                            if (FindCore.insertItem(level, storage, item)) {
                                break;
                            }
                        }
                        if (fluid != null) {
                            BlockPos[] tanks = FindCore.findFluidStorage(level, core);
                            for (BlockPos tank : tanks) {
                                if (FindCore.insertFluid(level, tank, fluid) == 0) {
                                    break;
                                }
                                //If we failed to insert all the fluid, too bad!
                            }
                        }
                    } else {
                        //LOGGER.info("stack is empty!");
                    }
                } else {
                    //LOGGER.info("Miner structure is not valid!");
                }
            } else {
                //LOGGER.warn("Owner is null!");
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
        if (this.owner != null) {
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

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Quantum Quarry");
    }

    public int getMode() {
        return this.getMode();
    }

    public void cycleMode() {
        this.mode++;
        if (this.mode > 2) {
            this.mode = 0;
        }
    }
}