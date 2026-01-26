package fr.caranouga.expeditech.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.caranouga.expeditech.client.screens.widgets.ProgressBarWidget;
import fr.caranouga.expeditech.client.utils.ColorUtils;
import fr.caranouga.expeditech.common.containers.CoalGeneratorMachineContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class CoalGeneratorMachineScreen extends ContainerScreen<CoalGeneratorMachineContainer> {
    private final ResourceLocation TEXTURE;
    private static final ResourceLocation PROGRESS_BAR_TEXTURE = modLocation("textures/gui/widgets/progress_bar.png");

    private final ProgressBarWidget progressBar;
    private final ProgressBarWidget energyBar;

    public CoalGeneratorMachineScreen(CoalGeneratorMachineContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        this.TEXTURE = modLocation("textures/gui/container/coal_generator.png");
        this.progressBar = new ProgressBarWidget(48, 18, 0x3da000);
        this.energyBar = new ProgressBarWidget(48, 62, 0xa02000);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        ColorUtils.setColor(1f, 1f, 1f, 1f);

        if(this.minecraft == null) return;
        this.minecraft.getTextureManager().bind(TEXTURE);

        renderBg(pMatrixStack, pPartialTicks, pX, pY, this.leftPos, this.topPos);
    }

    protected void renderBg(@Nonnull MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY, int i, int j){
        this.blit(pMatrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // Render the progress bar
        Minecraft.getInstance().getTextureManager().bind(PROGRESS_BAR_TEXTURE);

        this.energyBar.render(pMatrixStack, i, j, this.menu.getScaledEnergy());
        this.progressBar.render(pMatrixStack, i, j, this.menu.getScaledProgress());
    }

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(pMatrixStack);
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(pMatrixStack, pMouseX, pMouseY);
    }
}
