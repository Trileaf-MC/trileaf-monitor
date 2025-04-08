package cn.sanyeyun.trileafmonitormod;

import cn.sanyeyun.trileafmonitormod.item.ModItems;
import cn.sanyeyun.trileafmonitormod.item.ServerItems;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrileafMonitorMod implements ModInitializer {
	public static final String MOD_ID = "trileaf-monitor-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();

		// 调用 getServerInfoJson 方法获取服务器信息
		JsonObject serverInfoJson = ServerItems.getServerInfoJson();

		// 将服务器信息记录到日志中
		LOGGER.info("服务器信息: {}", serverInfoJson.toString());
	}
}
