package com.quantum.quantum_quarry.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quantum.quantum_quarry.world.inventory.QuantumMinerScreenMenu;

public class QuantumMinerScreenScreen extends AbstractContainerScreen<QuantumMinerScreenMenu> {
    private final static HashMap<String, Object> guistate = QuantumMinerScreenMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    Button button_empty;
    ImageButton imagebutton_redstone_resize;

    public QuantumMinerScreenScreen(QuantumMinerScreenMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 196;
    }

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/quantum_miner_screen.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
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
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/unlitredstonetorchresize.png"), this.leftPos + 7, this.topPos + 79, 0, 0, 16, 16, 16, 16);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/redstonetorchresize.png"), this.leftPos + 25, this.topPos + 80, 0, 0, 16, 16, 16, 16);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath("quantum_quarry", "textures/screens/energy_cell_level_0.png"), this.leftPos + 153, this.topPos + 81, 0, 0, 16, 16, 16, 16);
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
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_0"), 93, 18, -12829636, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_01"), 93, 28, -12829636, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.quantum_quarry.quantum_miner_screen.label_none"), 56, 39, -12829636, false);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void init() {
        super.init();
        button_empty = Button.builder(Component.translatable(""), e -> {
            if (true) {
                QuantumQuarryMod.PACKET_HANDLER.sendToServer(new QuantumMinerScreenButtonMessage(0, x, y, z));
                QuantumMinerScreenButtonMessage.handleButtonAction(entity, 0, x, y, z);
            }
        }).bounds(this.leftPos + 4, this.topPos + 57, 20, 20).build();
        guistate.put("button:button_empty", button_empty);
        this.addRenderableWidget(button_empty);
        //imagebutton_redstone_resize = new ImageButton(this.leftPos + 6, this.topPos + 59, 16, 16, 0, 0, 16, )
    }
}
