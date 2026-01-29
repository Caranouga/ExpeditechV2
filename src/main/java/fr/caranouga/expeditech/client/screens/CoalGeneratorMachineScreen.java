package fr.caranouga.expeditech.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.client.screens.widgets.ProgressBarWidget;
import fr.caranouga.expeditech.client.utils.ColorUtils;
import fr.caranouga.expeditech.common.containers.CoalGeneratorMachineContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

import java.util.Arrays;

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

    @Override
    protected void renderTooltip(@Nonnull MatrixStack pPoseStack, int pX, int pY) {
        if(pX >= this.leftPos + 48 && pX <= this.leftPos + 48 + ProgressBarWidget.WIDTH &&
                pY >= this.topPos + 62 && pY <= this.topPos + 62 + ProgressBarWidget.HEIGHT) {
            int energyStored = this.menu.getEnergyStored();
            int energyCapacity = this.menu.getMaxEnergyStored();

            ITextComponent translation = new TranslationTextComponent("tooltip." + Expeditech.MODID + ".coal_generator.energy", energyStored, energyCapacity);
            renderTooltip(pPoseStack, translation, pX, pY);
        }

        // TODO: Patch tooltip progress
        if(pX >= this.leftPos + 48 && pX <= this.leftPos + 48 + ProgressBarWidget.WIDTH &&
                pY >= this.topPos + 18 && pY <= this.topPos + 18 + ProgressBarWidget.HEIGHT) {
            int progress = this.menu.getProgress();
            int maxProgress = this.menu.getMaxProgress();
            float progressPercent = (int) (this.menu.getScaledProgress() * 100);

            ITextComponent translation = new TranslationTextComponent("tooltip." + Expeditech.MODID + ".coal_generator.progress", progress, maxProgress, progressPercent + "%");
            renderTooltip(pPoseStack, translation, pX, pY);
        }

        super.renderTooltip(pPoseStack, pX, pY);
    }
}
