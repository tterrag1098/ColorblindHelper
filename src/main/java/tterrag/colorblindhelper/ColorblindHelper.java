package tterrag.colorblindhelper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import codechicken.nei.guihook.GuiContainerManager;

import com.enderio.core.IEnderMod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import static tterrag.colorblindhelper.ColorblindHelper.*;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:endercore;required-after:NotEnoughItems")
public class ColorblindHelper implements IEnderMod
{
    public static final String MODID = "colorblindhelper";
    public static final String MOD_NAME = "Colorblind Helper";
    public static final String VERSION = "@VERSION@";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!event.getSide().isClient())
        {
            throw new RuntimeException("Colorblind Helper is client-only!");
        }

        ConfigReader.INSTANCE.preInit(event);
        
        // This line is no longer needed with the below hack
        // GuiContainerManager.addDrawHandler(ItemOverlayHandler.INSTANCE);
        
        // Sorry CB but this really needs some hooks -.-
        GuiContainerManager.drawItems = new RenderItem()
        {
            @Override
            public void renderItemIntoGUI(FontRenderer fr, TextureManager tm, ItemStack stack, int x, int y, boolean renderEffect)
            {
                ItemOverlayHandler.INSTANCE.preRenderStack(fr, stack, x, y);
                super.renderItemIntoGUI(fr, tm, stack, x, y, renderEffect);
                ItemOverlayHandler.INSTANCE.postRenderStack(fr, stack, x, y);
            }
        };
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ConfigReader.INSTANCE.refresh();
    }

    @Override
    public String modid()
    {
        return MODID;
    }

    @Override
    public String name()
    {
        return MOD_NAME;
    }

    @Override
    public String version()
    {
        return VERSION;
    }
}
