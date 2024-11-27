package com.quantum.quantum_quarry.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import com.quantum.quantum_quarry.QuantumQuarry;
import com.quantum.quantum_quarry.world.inventory.ScreenMenu;

public class Menus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, QuantumQuarry.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<ScreenMenu>> QUANTUM_MINER_SCREEN = REGISTRY.register(
        "quantum_miner_screen", 
        () -> IMenuTypeExtension.create(ScreenMenu::new)
    ); 
}