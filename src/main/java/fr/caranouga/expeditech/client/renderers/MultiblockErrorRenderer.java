package fr.caranouga.expeditech.client.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.caranouga.expeditech.client.utils.ColorUtils;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class MultiblockErrorRenderer {
    private final ArrayList<Marker> markers = new ArrayList<>();

    public void addMarker(BlockPos pos, int color, ITextComponent text, long duration) {
        markers.add(new Marker(pos, color, text, System.currentTimeMillis() + duration));
    }

    public void render() {
        long currentTime = System.currentTimeMillis();
        markers.removeIf(marker -> marker.removeAtTime <= currentTime);

        this.markers.forEach(marker -> {
            if (marker.getPos() != null) {
                renderMarker(marker);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void renderMarker(Marker marker) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        ColorUtils.setColor(0f, 1f, 0f, 0.75f);
        RenderSystem.disableTexture();

        DebugRenderer.renderFilledBox(marker.getPos(), 0F, marker.getR(), marker.getG(), marker.getB(), marker.getA());
        if (!marker.getText().isEmpty()) {
            double d0 = (double)marker.getPos().getX() + 0.5D;
            double d1 = (double)marker.getPos().getY() + 1.2D;
            double d2 = (double)marker.getPos().getZ() + 0.5D;
            DebugRenderer.renderFloatingText(marker.getText(), d0, d1, d2, -1, 0.01F, true, 0.0F, true);
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    private static class Marker {
        private final BlockPos pos;
        private final int color;
        private final ITextComponent text;
        private final long removeAtTime;

        public Marker(BlockPos pos, int color, ITextComponent text, long removeAtTime) {
            this.pos = pos;
            this.color = color;
            this.text = text;
            this.removeAtTime = removeAtTime;
        }

        public BlockPos getPos() {
            return pos;
        }

        public float getR() {
            return (float) (this.color >> 16 & 255) / 255.0F;
        }

        public float getG() {
            return (float) (this.color >> 8 & 255) / 255.0F;
        }

        public float getB() {
            return (float) (this.color & 255) / 255.0F;
        }

        public float getA() {
            return (float) (this.color >> 24 & 255) / 255.0F;
        }

        public String getText() {
            return text.getString();
        }
    }
}