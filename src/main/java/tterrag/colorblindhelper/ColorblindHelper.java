package tterrag.colorblindhelper;

import com.enderio.core.IEnderMod;
import com.enderio.core.common.event.ItemGUIRenderEvent;

import static tterrag.colorblindhelper.ColorblindHelper.*;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:endercore")
public class ColorblindHelper implements IEnderMod
{
    public static final String MODID = "colorblindhelper";
    public static final String MOD_NAME = "Colorblind Helper";
    public static final String VERSION = "@VERSION@";
    
//    TODO for when EnderCore gets the pre version
//    @SubscribeEvent
//    public void onItemRenderPre(ItemGUIRenderEvent.Pre event)
//    {
//        ItemOverlayHandler.INSTANCE.preRenderStack(Minecraft.getMinecraft().fontRendererObj, event.getStack(), event.getxPosition(), event.getyPosition());
//    }

    @SubscribeEvent
    public void onItemRenderPos(ItemGUIRenderEvent.Post event)
    {
        ItemOverlayHandler.INSTANCE.postRenderStack(Minecraft.getMinecraft().fontRendererObj, event.getStack(), event.getxPosition(), event.getyPosition());
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!event.getSide().isClient())
        {
            throw new RuntimeException("Colorblind Helper is client-only!");
        }

        ConfigReader.INSTANCE.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
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
