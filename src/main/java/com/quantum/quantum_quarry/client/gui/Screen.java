package com.quantum.quantum_quarry.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quantum.quantum_quarry.world.inventory.ScreenMenu;
import com.quantum.quantum_quarry.block.entity.QuarryBlockEntity;

public class Screen extends AbstractContainerScreen<ScreenMenu> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Screen.class);
    private final static HashMap<String, Object> guistate = ScreenMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private final QuarryBlockEntity quarryEntity;
    Button button_mode;

    public Screen(ScreenMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 196;
        this.quarryEntity = container.getQuarryEntity();
        if (this.quarryEntity == null) {
            LOGGER.info("Block at {} has no entity!", new BlockPos(this.x, this.y, this.z));
        }
        if (container != null) {
            if (container.world == null) {
                LOGGER.warn("World is null!");
            }
            if (container.entity == null) {
                LOGGER.warn("Entity is null!");
            }
            LOGGER.info("Container Location: {}", new BlockPos(this.x, this.y, this.z));
            if (container.boundBlockEntity == null) {
                LOGGER.warn("Block entity is null!");
            }
        } else {
            LOGGER.info("We have a big problem...");
        }
    }

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/quantum_miner_screen.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.quarryEntity != null) {
            switch (this.quarryEntity.getMode()) {
                case 0:
                    guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/redstone_resize.png"), this.leftPos + 7, this.topPos + 45, 0, 0, 16, 16, 16, 16);
                    break;
                case 1:
                    guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/redstonetorchresize.png"), this.leftPos + 7, this.topPos + 45, 0, 0, 16, 16, 16, 16);
                    break;
                case 2:
                    guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/unlitredstonetorchresize.png"), this.leftPos + 7, this.topPos + 45, 0, 0, 16, 16, 16, 16);
                    break;
            }
        } else {
            LOGGER.info("Screen has unbound quarry entity!");
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/biome_marker_blank.png"), this.leftPos + 6, this.topPos + 38, 0, 0, 16, 16, 16, 16);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/enchanted_book_skeleton.png"), this.leftPos + 6, this.topPos + 18, 0, 0, 16, 16, 16, 16);
        //guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/unlitredstonetorchresize.png"), this.leftPos + 7, this.topPos + 79, 0, 0, 16, 16, 16, 16);
        //guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/redstonetorchresize.png"), this.leftPos + 25, this.topPos + 80, 0, 0, 16, 16, 16, 16);
        //guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/energy_cell_level_0.png"), this.leftPos + 153, this.topPos + 81, 0, 0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_quantum_miner"), 6, 4, -12829636, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_quarry_level"), 24, 17, -12829636, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_blocks_mined"), 24, 28, -12829636, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_biome"), 24, 39, -12829636, false);
        guiGraphics.drawString(this.font, Component.literal("X"), 93, 18, -12829636, false);
        guiGraphics.drawString(this.font, Component.literal(this.quarryEntity == null ? "X" : String.valueOf(this.quarryEntity.manager.minedBlocks)), 93, 28, -12829636, false);
        guiGraphics.drawString(this.font, Component.literal(this.quarryEntity == null ? "X" : this.quarryEntity.manager.currentBiome.toString()), 56, 39, -12829636, false);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void init() {
        super.init();
        button_mode = Button.builder(Component.translatable("gui.quantum_quarry.quantum_miner_screen.button_empty"), e -> {
            if (this.quarryEntity != null) {
                this.quarryEntity.cycleMode();
            }
        }).bounds(this.leftPos + 4, this.topPos + 57, 20, 20).build();
        guistate.put("button:button_mode", button_mode);
        this.addRenderableWidget(button_mode);
    }
}
