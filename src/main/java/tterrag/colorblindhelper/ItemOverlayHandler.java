package tterrag.colorblindhelper;

import java.awt.Color;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.nei.guihook.IContainerDrawHandler;

import com.enderio.core.client.render.ColorUtil;

public enum ItemOverlayHandler implements IContainerDrawHandler
{
    INSTANCE;

    @Override
    public void onPreDraw(GuiContainer arg0)
    {
        ;
    }

    @Override
    public void postRenderObjects(GuiContainer arg0, int arg1, int arg2)
    {
        ;
    }

    @Override
    public void renderObjects(GuiContainer arg0, int arg1, int arg2)
    {
        ;
    }

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot)
    {
        ItemStack stack = slot.getStack();
        preRenderStack(Minecraft.getMinecraft().fontRenderer, stack, slot.xDisplayPosition, slot.yDisplayPosition);
    }

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot)
    {
        ItemStack stack = slot.getStack();
        postRenderStack(Minecraft.getMinecraft().fontRenderer, stack, slot.xDisplayPosition, slot.yDisplayPosition);
    }

    public void preRenderStack(FontRenderer fr, ItemStack stack, int x, int y)
    {
        ItemConfig config = ConfigReader.INSTANCE.getConfig(stack);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setColorRGBA_I(ColorUtil.getRGB(config.getUnderlay()), config.getUnderlay().getAlpha());
        Tessellator.instance.addVertex(x, y, 0);
        Tessellator.instance.addVertex(x, y + 16, 0);
        Tessellator.instance.addVertex(x + 16, y + 16, 0);
        Tessellator.instance.addVertex(x + 16, y, 0);
        Tessellator.instance.draw();

        GL11.glPopAttrib();
    }

    public void postRenderStack(FontRenderer fr, ItemStack stack, int x, int y)
    {
        ItemConfig config = ConfigReader.INSTANCE.getConfig(stack);
        String overlay = config.getOverlay();
        Color color = config.getOverlayColor();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glTranslatef(0, 0, 100);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        if (overlay.startsWith("#"))
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());
            String shapeStr = overlay.substring(1).toUpperCase(Locale.US);
            ShapeWidget widget = ShapeWidget.valueOf(shapeStr);
            widget.getMap().render(widget, x, y, 8, 8, 0, true);
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
         
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glScalef(scale, scale, 0);
            fr.drawStringWithShadow(overlay, (int) (x * (1 / scale)), (int) (y * (1 / scale)), color.getRGB());
        }
        
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
