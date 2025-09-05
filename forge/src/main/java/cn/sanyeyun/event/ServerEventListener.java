package cn.sanyeyun.event;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.constant.CommonConstants;
import cn.sanyeyun.entity.ModInfo;
import cn.sanyeyun.entity.ServerInfo;
import cn.sanyeyun.entity.response.RuoYiResponse;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.service.ModService;
import cn.sanyeyun.utils.HttpRequestUtil;
import cn.sanyeyun.utils.RetryUtils;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import com.google.gson.GsonBuilder;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import cn.sanyeyun.TrileafMonitorForgeMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cn.sanyeyun.constant.CommonConstants.SERVERS_REGISTER;

/**
 * 服务器事件监听
 *
 * @author 徐亚松
 * 2025-04-14 11:19
 **/
@Mod.EventBusSubscriber(modid = TrileafMonitorForgeMod.MOD_ID)
public class ServerEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEventListener.class);
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        TrileafMonitorForgeMod.LOGGER.info("服务器已启动,开始监控...");
        // 处理服务器启动事件
        handleServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        TrileafMonitorForgeMod.LOGGER.info("服务器正在停止,结束监控...");
    }

    public static void register() {
        // 在Forge中，使用@Mod.EventBusSubscriber注解后不需要手动注册
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("服务端启动中,准备获取Mod信息");
        // 异步执行采集Mod信息
        CompletableFuture.runAsync(ModService::collectModInfo);
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
            ServerInfo serverInfo = buildFrom(server);
            String json = GSON.toJson(serverInfo);
            String responseStr = HttpRequestUtil.post(SERVERS_REGISTER, json, PlatformType.INTERNAL);
            RuoYiResponse response = GSON.fromJson(responseStr, RuoYiResponse.class);
            // 等待服务器顺利注册后 再去上传Mod信息
            if (response.isSuccess()) {
                List<ModInfo> modInfos = RetryUtils.retryUntilNotNull(() -> {
                    List<ModInfo> list = GlobalCache.getModInfos();
                    return (list != null && !list.isEmpty()) ? list : null;
                }, 5, 5000); // 重试5次，每次间隔5秒

                if (modInfos != null) {
                    HttpRequestUtil.postMultipart(CommonConstants.MOD_REGISTER, GSON.toJson(modInfos), GlobalCache.getCompletelyUnmatchedFiles(),PlatformType.INTERNAL);
                } else {
                    LOGGER.warn("未能在重试后获取到 ModInfos，跳过上传");
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
            DedicatedServerProperties properties = dedicatedServer.getProperties();

            GlobalCache.setServerPort((long) properties.serverPort);
            info.setServerPort((long) properties.serverPort);
            info.setGameMode(properties.gamemode.getName());
            info.setDifficulty(properties.difficulty.getSerializedName());
            info.setIsPvp(properties.pvp ? 1 : 0);
            info.setIsOnlineMode(booleanConvertInteger(properties.onlineMode));
            info.setIsEnforceWhitelist(booleanConvertInteger(properties.enforceWhitelist));
            info.setHasEnableQuery(booleanConvertInteger(properties.enableQuery));
            info.setMotd(properties.motd);
        }

        // 补充运行时数据
        info.setMinecraftVersion(server.getServerVersion());
        info.setCoreType(server.getServerModName());
        info.setCoreVersion(server.getServerVersion()); // 设置为指定的Forge版本
        String publicIp = HttpRequestUtil.getPublicIp();
        GlobalCache.setServerIp(publicIp);
        info.setServerIp(publicIp);
        info.setCurrentPlayers((long) server.getPlayerCount());
        info.setMaxPlayers((long) server.getMaxPlayers());
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
