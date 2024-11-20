package com.quantum.quantum_quarry.block.entity.MinerBlockEntity;

public class MinerBlockEntity extends BlockEntity {
    public MinerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MINER_BLOCK_ENTITY.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return this.getData(ENERGY_STORAGE);
    }

    public void transferEnergyToQuarry(ServerLevel serverLevel) {
        EnergyStorage minerEnergy = this.getEnergyStorage();
        BlockPos quarryPos = FindCore.execute();
        BlockEntity blockEntity = serverLevel.getBlockEntity(quarryPos);
        if (blockEntity instanceof QuarryBlockEntity quarryEntity) {
            EnergyStorage quarryStorage = quarryEntity.getEnergyStorage();
            int energyToTransfer = Math.min(minerEnergy.getEnergyStored(), quarryE)
        }
    }
}