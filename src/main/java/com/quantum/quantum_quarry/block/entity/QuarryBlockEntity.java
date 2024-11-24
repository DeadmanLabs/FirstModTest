package com.quantum.quantum_quarry.block.entity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantum.quantum_quarry.block.QuarryBlock;
import com.quantum.quantum_quarry.helpers.AlternateDimension.ChunkMiner;
import com.quantum.quantum_quarry.init.BlockEntities;
import com.quantum.quantum_quarry.procedures.FindCore;
import com.quantum.quantum_quarry.world.inventory.ScreenMenu;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import java.util.Map;
import java.util.HashMap;

public class QuarryBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarryBlockEntity.class);
    private UUID owner;
    private ChunkMiner manager;
    public static final int TANK_CAPACITY = 20000;
    private Queue<FluidStack> fluidStorage = new LinkedList<>();
    public BlockPos location;
    private final Map<BlockPos, Boolean> minerRedstoneStates = new HashMap<>();
    private ItemStack bookSlot;
    private ItemStack biomeSlot;

    public int mode;
    public int blocksMined;
    public String biomeText;

    private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
    private final SidedInvWrapper handler = new SidedInvWrapper(this, null);

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.QUARRY_BLOCK_ENTITY.get(), pos, state);
        this.location = pos;
        this.mode = 0;
        this.blocksMined = 0;
        this.biomeText = "";
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
        if (this.level instanceof ServerLevel serverLevel) {
            this.manager = new ChunkMiner(serverLevel); //null biome = all
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, QuarryBlockEntity blockEntity) {
        if (!level.isClientSide && blockEntity.owner != null && level instanceof ServerLevel serverLevel && blockEntity.manager != null) {
            Player player = serverLevel.getPlayerByUUID(blockEntity.owner);
            if (player != null) {
                BlockPos core = FindCore.execute(level, pos.getX(), pos.getY(), pos.getZ()); //ensure that we are using the core for validation
                if (FindCore.validateStructure(level, core)) {
                    if (
                        evaluateRedstone(blockEntity) && 
                        blockEntity.energyStorage.extractEnergy(1, true) == 1
                    ) {
                        boolean canMine = true;
                        if (!blockEntity.manager.itemsToGive.isEmpty()) {
                            BlockPos[] storages = FindCore.findStorage(level, core);
                            canMine = blockEntity.manager.itemsToGive.removeIf(item -> {
                                boolean inserted = false;
                                for (BlockPos storage: storages) {
                                    if (FindCore.insertItem(level, storage, item)) {
                                        inserted = true;
                                        break;
                                    }
                                }
                                if (!inserted) {
                                    LOGGER.warn("Failed to insert {} x {} anywhere! Halting mining!", item.getCount(), item.getDisplayName());
                                    return false;
                                }
                                return true;
                            });
                        }
                        if (!blockEntity.manager.fluidsToGive.isEmpty()) {
                            BlockPos[] tanks = FindCore.findFluidStorage(level, core);
                            canMine = blockEntity.manager.fluidsToGive.removeIf(fluid -> {
                                boolean inserted = false;
                                for (BlockPos tank : tanks) {
                                    int remaining = FindCore.insertFluid(level, tank, fluid);
                                    if (remaining == 0) {
                                        inserted = true;
                                        break;
                                    } else {
                                        fluid.setAmount(remaining);
                                    }
                                }
                                if (!inserted) {
                                    LOGGER.warn("Could not insert fluid: {} (remaining: {} mb)", fluid.getFluidType(), fluid.getAmount());
                                    //return false;
                                }
                                return true;
                            });
                        }
                        if (canMine) {
                            boolean mined = blockEntity.manager.mineNextBlock(blockEntity.bookSlot);
                            if (!mined) {
                                blockEntity.manager.startMining(blockEntity.bookSlot, blockEntity.biomeSlot);
                                mined = blockEntity.manager.mineNextBlock(blockEntity.bookSlot);
                            }
                            if (mined) {
                                blockEntity.energyStorage.extractEnergy(1, false);
                                blockEntity.manager.minedBlocks++;
                                blockEntity.blocksMined = blockEntity.manager.minedBlocks;
                                blockEntity.level.sendBlockUpdated(blockEntity.worldPosition, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
                            } else {
                                LOGGER.warn("No blocks available to mine or no valid mining target!");
                            }
                        } else {
                            LOGGER.info("Mining Halted: storage unavailable for items or fluids.");
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
    protected void saveAdditional(CompoundTag tag, Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.stacks, provider);
        }
        tag.put("energyStorage", energyStorage.serializeNBT(provider));
        tag.putInt("mode", this.mode);
        tag.putInt("mined", this.blocksMined);
        tag.putString("BiomeText", this.biomeText);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider provider) {
        super.loadAdditional(tag, provider);
        if (!this.tryLoadLootTable(tag)) {
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, this.stacks, provider);
        if (tag.get("energyStorage") instanceof IntTag intTag) {
            energyStorage.deserializeNBT(provider, intTag);
        }
        this.mode = tag.getInt("mode");
        this.blocksMined = tag.getInt("mined");
        this.biomeText = tag.getString("BiomeText");
    }

    public void cycleMode() {
        this.mode++;
        if (this.mode > 2) {
            this.mode = 0;
        }
        if (!this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider provider) {
        super.onDataPacket(net, pkt, provider);
        this.loadAdditional(pkt.getTag(), provider);
    }

    @Override 
    public CompoundTag getUpdateTag(Provider lookupProvider) {
        CompoundTag tag = super.getUpdateTag(lookupProvider);
        this.saveAdditional(tag, lookupProvider);
        return tag;
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override 
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Component getDefaultName() {
        return Component.literal("quantum_quarry");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ScreenMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Quantum Quarry");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (index == 0)
            return false;
        if (index == 1)
            return false;
        return true;
    }

    public SidedInvWrapper getItemHandler() {
        return handler;
    }

    private final EnergyStorage energyStorage = new EnergyStorage(200000, 200000, 200000, 0) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int retval = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) {
                setChanged();
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
            }
            return retval;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int retval = super.extractEnergy(maxExtract, simulate);
            if (!simulate) {
                setChanged();
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
            }
            return retval;
        }
    };

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    private static boolean evaluateRedstone(QuarryBlockEntity entity) {
        //LOGGER.info("Checking Mode {} with signal {}", mode, isReceiving);
        switch (entity.mode) {
            case 0:
                return true;
            case 1:
                return entity.minerRedstoneStates.values().stream().anyMatch(Boolean::booleanValue);
            case 2:
                return !(entity.minerRedstoneStates.values().stream().anyMatch(Boolean::booleanValue));
            default:
                return false;
        }
    }

    public void updateMinerState(BlockPos minerPos, boolean isPowered) {
        LOGGER.info("Miner @ {} Changed Redstone State to Powered = {}", minerPos, isPowered);
        minerRedstoneStates.put(minerPos, isPowered);
    }

    public void setEnchants(ItemStack enchantedBook) {
        this.bookSlot = enchantedBook;
    }

    public void setBiome(ItemStack biomeMarker) {
        this.biomeSlot = biomeMarker;
    }
}