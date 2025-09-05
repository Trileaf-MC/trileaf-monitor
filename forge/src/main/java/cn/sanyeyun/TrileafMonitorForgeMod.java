package cn.sanyeyun;

import cn.sanyeyun.command.TrileafCommand;
import cn.sanyeyun.event.PlayerEventListener;
import cn.sanyeyun.event.ServerEventListener;
import cn.sanyeyun.utils.ConfigFileManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TrileafMonitorForgeMod.MOD_ID)
public class TrileafMonitorForgeMod {
    public static final String MOD_ID = "trileafmonitor";
    public static final Logger LOGGER = LoggerFactory.getLogger(TrileafMonitorForgeMod.class);

    public TrileafMonitorForgeMod() {
        LOGGER.info("TrileafMonitor mod 初始化中...");
        
        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册setup方法到模组事件总线
        modEventBus.addListener(this::setup);

        // 注册事件监听器到Forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("TrileafMonitor mod 设置中...");

        // 加载配置
         ConfigFileManager.loader();

    }
}
