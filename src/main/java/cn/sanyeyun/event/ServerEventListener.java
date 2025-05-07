package cn.sanyeyun.event;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.constant.CommonConstants;
import cn.sanyeyun.entity.ModInfo;
import cn.sanyeyun.entity.ServerInfo;
import cn.sanyeyun.entity.response.RuoYiResponse;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.service.ModService;
import cn.sanyeyun.utils.HttpRequestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cn.sanyeyun.constant.CommonConstants.SERVERS_REGISTER;

/**
 * 服务器事件监听
 *
 * @author 徐亚松
 * 2025-04-14 11:19
 **/
public class ServerEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEventListener.class);
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) -> {
            LOGGER.info("服务端启动中,准备获取Mod信息");
            // 异步执行采集Mod信息
            CompletableFuture.runAsync(ModService::collectModInfo);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
            LOGGER.info("服务端关闭中");
            CompletableFuture.runAsync(()-> HttpRequestUtil.put(CommonConstants.SERVERS_LOG_OUT, null, PlatformType.INTERNAL));
        });

        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            LOGGER.info("服务端已启动,准备获取服务器信息");
            // 异步执行采集与上报逻辑
            CompletableFuture.runAsync(() -> handleServerStarted(server));
        });

    }

    /**
     * 注册服务器信息
     *
     * @param server 服务器实例
     * @author 徐亚松
     * <p>2025/5/7 21:07</p>
     */
    private static void handleServerStarted(MinecraftServer server) {
        try {
            String json = GSON.toJson(buildFrom(server));
            String responseStr = HttpRequestUtil.post(SERVERS_REGISTER, json, PlatformType.INTERNAL);
            RuoYiResponse response = GSON.fromJson(responseStr, RuoYiResponse.class);
            // 等待服务器顺利注册后 再去上传Mod信息
            if (response.isSuccess()) {
                List<ModInfo> modInfos = GlobalCache.getModInfos();
                // 获取到Mod后再去请求,否则重新获取Mod
                if (modInfos != null && !modInfos.isEmpty() ){
                    HttpRequestUtil.postMultipart(CommonConstants.MOD_REGISTER, GSON.toJson(modInfos), GlobalCache.getCompletelyUnmatchedFiles());
                }
            }

        } catch (Exception e) {
            LOGGER.error("服务器信息上传失败", e);
        }
    }


    /**
     * 构建参数
     *
     * @param server 实例信息
     * @return {@link ServerInfo}
     * @author 徐亚松
     * <p>2025/4/14 22:25</p>
     */
    private static ServerInfo buildFrom(MinecraftServer server) {
        ServerInfo info = new ServerInfo();

        // 转为服务端的配置文件
        if (server instanceof DedicatedServer dedicatedServer) {
            ServerPropertiesHandler properties = dedicatedServer.getProperties();

            GlobalCache.setServerPort((long) properties.serverPort);
            info.setServerPort((long) properties.serverPort);
            info.setGameMode(properties.gameMode.getName());
            info.setDifficulty(properties.difficulty.getName());
            info.setIsPvp(properties.pvp ? 1 : 0);
            info.setIsOnlineMode(booleanConvertInteger(properties.onlineMode));
            info.setIsEnforceWhitelist(booleanConvertInteger(properties.enforceWhitelist));
            info.setHasEnableQuery(booleanConvertInteger(properties.enableQuery));
            info.setMotd(properties.motd);
        }

        // 补充运行时数据
        info.setMinecraftVersion(server.getVersion());
        info.setCoreType(server.getServerModName());
        info.setCoreVersion(server.getVersion());
        String publicIp = HttpRequestUtil.getPublicIp();
        GlobalCache.setServerIp(publicIp);
        info.setServerIp(publicIp);
        info.setCurrentPlayers((long) server.getCurrentPlayerCount());
        info.setMaxPlayers((long) server.getMaxPlayerCount());
        info.setJavaVersion(System.getProperty("java.version"));
        // 图标
        /*Path iconPath = Paths.get("server-icon.png");
        if (Files.exists(iconPath)) {
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(iconPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String base64 = Base64.getEncoder().encodeToString(bytes);
            info.setIcon("data:image/png;base64," + base64);
        }*/


        return info;
    }

    /**
     * boolean 转 int
     *
     * @param b 值
     * @return int
     * @author 徐亚松 2025/4/14 16:39
     */
    private static int booleanConvertInteger(boolean b) {
        return b ? 1 : 0;
    }

}
