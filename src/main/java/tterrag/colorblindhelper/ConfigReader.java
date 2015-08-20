package tterrag.colorblindhelper;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.enderio.core.common.Handlers.Handler;
import com.enderio.core.common.Handlers.Handler.HandlerType;
import com.enderio.core.common.config.JsonConfigReader;
import com.enderio.core.common.config.JsonConfigReader.ModToken;
import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.ItemUtil;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(HandlerType.FML)
public enum ConfigReader
{
    INSTANCE;

    private static class JsonData
    {
        public String stack;
        public String underlay = "0";
        public String overlay = "";
        public String overlayColor = "FFFFFFFF";
    }

    private JsonConfigReader<JsonData> reader;
    private ArrayList<Object> configs;

    private File configDir;
    private File json;

    public void preInit(FMLPreInitializationEvent event)
    {
        configDir = event.getModConfigurationDirectory();
        json = new File(configDir, "colorblind.json");
    }

    public void refresh()
    {
        configs = Lists.newArrayList();
        
        if (reader == null)
        {
            ModToken token = new ModToken(ColorblindHelper.class, ColorblindHelper.MODID + "/config");
            reader = new JsonConfigReader<JsonData>(token, json, JsonData.class);
        }
        
        reader.refresh();

        List<JsonData> all = reader.getElements();
        for (JsonData data : all)
        {
            ItemStack stack = ItemUtil.parseStringIntoItemStack(data.stack);
            ItemKey key = ItemKey.forStack(stack);
            
            int underlayColor = Integer.parseUnsignedInt(data.underlay, 16);
            int overlayColor = Integer.parseUnsignedInt(data.overlayColor, 16);
            
            ItemConfig config = new ItemConfig(new Color(underlayColor, true), data.overlay, new Color(overlayColor, true));
            configs.add(key);
            configs.add(config);
        }
    }

    @SubscribeEvent
    public void onConfigFileReload(ConfigFileChangedEvent event)
    {
        if (event.modID.equals(ColorblindHelper.MODID))
        {
            if (Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION))
            {
                refresh();
            }
            event.setSuccessful();
        }
    }

    private static final ItemConfig DEFAULT = new ItemConfig(new Color(0, true), "", new Color(0, true));
    public ItemConfig getConfig(ItemStack stack)
    {
        int idx = configs.indexOf(ItemKey.forStack(stack));
        if (idx < 0)
        {
            return DEFAULT;
        }
        return (ItemConfig) configs.get(idx + 1);
    }
}
