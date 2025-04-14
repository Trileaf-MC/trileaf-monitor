package cn.sanyeyun;

import cn.sanyeyun.item.ModItems;
import cn.sanyeyun.item.PluginUtils;
import cn.sanyeyun.item.ServerItems;
import cn.sanyeyun.item.TokenJsonGenerator;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.sanyeyun.item.TokenJsonGenerator.generateRandomToken;

public class TrileafMonitorMod implements ModInitializer {
	public static final String MOD_ID = "trileaf-monitor-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();
		PluginUtils.getPluginInfo();
		ServerItems.getServerInfoJson();
		TokenJsonGenerator.createTokenJsonFile(generateRandomToken());




	}


}
