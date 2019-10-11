package com.minecraftmods.onemod.blocks;

import com.minecraftmods.onemod.OneMod;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/** @author Mshnik */
final class FirstBlockScreen extends ContainerScreen<FirstBlockContainer> {
  private ResourceLocation FIRST_BLOCK_GUI_TEXTURE =
      new ResourceLocation(OneMod.MODID, "textures/gui/firstblock_gui.png");

  FirstBlockScreen(
      FirstBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
  }

  /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    font.drawString(title.getFormattedText(), 8.0F, 6.0F, 4210752);
    font.drawString(
        playerInventory.getDisplayName().getFormattedText(),
        8.0F,
        (float) (ySize - 96 + 2),
        4210752);
  }

  /** Draws the background layer of container (behind the items). */
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bindTexture(FIRST_BLOCK_GUI_TEXTURE);
    int i = (width - xSize) / 2;
    int j = (height - ySize) / 2;
    blit(i, j, 0, 0, xSize, ySize);
  }
}
