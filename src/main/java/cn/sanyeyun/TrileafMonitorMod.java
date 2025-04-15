package cn.sanyeyun;

import cn.sanyeyun.event.PlayerEventListener;
import cn.sanyeyun.event.ServerEventListener;
import cn.sanyeyun.utils.ConfigFileManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrileafMonitorMod implements ModInitializer {
    public static final String MOD_ID = "trileaf-monitor-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        ConfigFileManager.loader();
        ServerEventListener.register();
        PlayerEventListener.register();
        // ConfigFileManager.createTokenJsonFile(generateRandomToken());
    }
}
