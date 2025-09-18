package cn.sanyeyun;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.command.TrileafCommand;
import cn.sanyeyun.event.PlayerEventListener;
import cn.sanyeyun.event.ServerEventListener;
import cn.sanyeyun.utils.ConfigFileManager;
import cn.sanyeyun.utils.KeyPairUtil;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrileafMonitorMod implements ModInitializer {
    public static final String MOD_ID = "trileaf-monitor-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(TrileafMonitorMod.class);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");
        ConfigFileManager.loader();

        KeyPairUtil.loadOrGenerateKeyPair();
        ServerEventListener.register();
        PlayerEventListener.register();
        TrileafCommand.register(); // ← 注册命令
        // ConfigFileManager.createTokenJsonFile(generateRandomToken());
    }
}
