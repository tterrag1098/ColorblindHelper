package tterrag.colorblindhelper;

import java.awt.Color;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

public enum ItemOverlayHandler
{
    INSTANCE;

    public void preRenderStack(FontRenderer fr, ItemStack stack, int x, int y)
    {
        ItemConfig config = ConfigReader.INSTANCE.getConfig(stack);
        
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        
        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        buf.pos(x, y, 0).endVertex();
        buf.pos(x, y + 16, 0).endVertex();
        buf.pos(x + 16, y + 16, 0).endVertex();
        buf.pos(x + 16, y, 0).endVertex();

        Color color = config.getUnderlay();
        Vector4f vec = ColorUtil.toFloat(color);
        GlStateManager.color(vec.x, vec.y, vec.z, vec.w);

        Tessellator.getInstance().draw();
        
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public void postRenderStack(FontRenderer fr, ItemStack stack, int x, int y)
    {
        ItemConfig config = ConfigReader.INSTANCE.getConfig(stack);
        String overlay = config.getOverlay();
        Color color = config.getOverlayColor();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 100);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        
        if (overlay.startsWith("#"))
        {
            GlStateManager.enableBlend();
            Vector4f vec = ColorUtil.toFloat(color);

            GlStateManager.color(vec.x, vec.y, vec.z, vec.w);
            String shapeStr = overlay.substring(1).toUpperCase(Locale.US);
            ShapeWidget widget = ShapeWidget.valueOf(shapeStr);
            widget.getMap().render(widget, x, y, 8, 8, 0, true);
            
            GlStateManager.disableBlend();
        }
        else
        {
            int strWidth = fr.getStringWidth(overlay);
            float scale = 1;
            if (strWidth > 16)
            {
                scale = 16f / strWidth;
            }
            if (scale < 1 && scale > 2 / 3f)
            {
                scale = 2 / 3f;
            }
         
            GlStateManager.disableBlend();
            GlStateManager.scale(scale, scale, 0);
            
            fr.drawStringWithShadow(overlay, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color.getRGB());
        }
        
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
    }
}
