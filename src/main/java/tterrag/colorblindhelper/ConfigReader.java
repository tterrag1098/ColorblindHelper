package tterrag.colorblindhelper;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import net.minecraft.item.ItemStack;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import com.enderio.core.common.Handlers.Handler;
import com.enderio.core.common.Handlers.Handler.HandlerType;
import com.enderio.core.common.config.JsonConfigReader;
import com.enderio.core.common.config.JsonConfigReader.ModToken;
import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.EnderFileUtils;
import com.enderio.core.common.util.ItemUtil;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
        
        private JsonData copy()
        {
            JsonData ret = new JsonData();
            ret.stack = stack;
            ret.underlay = underlay;
            ret.overlay = overlay;
            ret.overlayColor = overlayColor;
            return ret;
        }
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
    
    public static final JsonData DEFAULT_DATA = new JsonData();

    @SneakyThrows
    public void refresh()
    {
        configs = Lists.newArrayList();

        if (reader == null)
        {
            ModToken token = new ModToken(ColorblindHelper.class, ColorblindHelper.MODID + "/config");
            reader = new JsonConfigReader<JsonData>(token, json, JsonData.class);
        }

        reader.refresh();
        List<JsonData> custom = reader.getElements("custom");
        
        json.delete();
        EnderFileUtils.copyFromJar(ColorblindHelper.class, ColorblindHelper.MODID + "/config/colorblind.json", json);
        reader.refresh();

        JsonElement object = new JsonParser().parse(FileUtils.readFileToString(json));
        JsonArray customData = object.getAsJsonObject().getAsJsonArray("custom");
        
        for (JsonData data : custom)
        {
            JsonData toWrite = data.copy();
            if (toWrite.overlay.equals(DEFAULT_DATA.overlay))
                toWrite.overlay = null;
            if (toWrite.overlayColor.equals(DEFAULT_DATA.overlayColor))
                toWrite.overlayColor = null;
            if (toWrite.underlay.equals(DEFAULT_DATA.underlay))
                toWrite.underlay = null;
            customData.add(new Gson().toJsonTree(toWrite, JsonData.class));
        }
        
        EnderFileUtils.writeToFile(json.getAbsolutePath(), new GsonBuilder().setPrettyPrinting().create().toJson(object));
        
        parseAll(reader.getElements("defaults"));
        parseAll(custom);
    }

    private void parseAll(Iterable<JsonData> entries)
    {
        for (JsonData data : entries)
        {
            try
            {
                ItemStack stack = ItemUtil.parseStringIntoItemStack(data.stack);
                ItemKey key = ItemKey.forStack(stack);

                int underlayColor = UnsignedInts.parseUnsignedInt(data.underlay, 16);
                int overlayColor = UnsignedInts.parseUnsignedInt(data.overlayColor, 16);

                ItemConfig config = new ItemConfig(new Color(underlayColor, true), data.overlay, new Color(overlayColor, true));

                if (configs.contains(key))
                {
                    int idx = configs.indexOf(key);
                    configs.set(idx, key);
                    configs.set(idx + 1, config);
                }
                else
                {
                    configs.add(key);
                    configs.add(config);
                }
            }
            catch (IllegalArgumentException e)
            {
                LogManager.getLogger(ColorblindHelper.MOD_NAME).info(data.stack + " could not be parsed into an ItemStack. Skipping...");
            }
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
