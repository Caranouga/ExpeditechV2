package fr.caranouga.expeditech.client.screens.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.caranouga.expeditech.client.utils.ColorUtils;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class ProgressBarWidget {
    private static final ResourceLocation PROGRESS_BAR_TEXTURE = modLocation("textures/gui/widgets/progress_bar.png");

    private static final int HEIGHT = 6;
    private static final int WIDTH = 80;

    private final int x;
    private final int y;
    private final int color;

    public ProgressBarWidget(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void render(MatrixStack matrixStack, int i, int j, float progress) {
        setColor();

        Minecraft.getInstance().getTextureManager().bind(PROGRESS_BAR_TEXTURE);

        AbstractGui.blit(matrixStack, i + this.x, j + this.y, 0, 0, (int) (progress * WIDTH), HEIGHT, WIDTH, HEIGHT);

        resetColor();
    }

    protected void setColor() {
        float r = ((this.color >> 16) & 0xFF) / 255f;
        float g = ((this.color >> 8) & 0xFF) / 255f;
        float b = (this.color & 0xFF) / 255f;

        ColorUtils.setColor(r,g,b,1f);
    }

    protected void resetColor() {
        ColorUtils.setColor(1f,1f,1f,1f);
    }
}
