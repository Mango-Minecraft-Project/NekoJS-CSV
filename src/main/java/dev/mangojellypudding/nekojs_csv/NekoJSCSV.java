package dev.mangojellypudding.nekojs_csv;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(NekoJSCSV.MODID)
public class NekoJSCSV {
    public static final String MODID = "nekojs_csv";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NekoJSCSV(IEventBus modEventBus, ModContainer modContainer) {}
}
