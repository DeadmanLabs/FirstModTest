package com.quantum.quantum_quarry.init.BlockCapabilities;

import net.neoforged.neoforge.registries.NeoForgeRegistries;

import com.quantum.quantum_quarry.QuantumQuarry;

public class BlockCapabilities {
    private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, QuantumQuarry.MODID);
    public static final Supplier<AttachmentType<EnergyStorage>> ENERGY_STORAGE = REGISTRY.register(
        "energy_storage",
        () -> AttachmentType.serializable(() -> new EnergyStorage(200000)).build()
    );
}