package cn.sanyeyun.event;

import cn.sanyeyun.TrileafMonitorForgeMod;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("服务端启动中,准备获取Mod信息");
        // 异步执行采集Mod信息
        ModService.collectModInfoAsync();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("服务端关闭中");
        CompletableFuture.runAsync(() ->
                HttpRequestUtil.put(CommonConstants.SERVERS_LOG_OUT, null, PlatformType.INTERNAL)
        );
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("服务端已启动,准备注册服务器信息");
        handleServerStarted(event.getServer());
    }

    private static void handleServerStarted(MinecraftServer server) {
        try {
            String json = GSON.toJson(buildFrom(server));

            CompletableFuture.supplyAsync(() ->
                    GSON.fromJson(HttpRequestUtil.post(SERVERS_REGISTER, json, PlatformType.INTERNAL), RuoYiResponse.class)
            ).thenAccept(response -> {
                if (response != null && response.isSuccess()) {
                    LOGGER.info("服务器注册成功,等待 Mod 信息采集完成...");

                    CompletableFuture.allOf(GlobalCache.getModInfos(), GlobalCache.getCompletelyUnmatchedFiles())
                            .thenRun(() -> {
                                try {
                                    List<ModInfo> modInfos = GlobalCache.getModInfos().join();
                                    List<File> files = GlobalCache.getCompletelyUnmatchedFiles().join();

                                    if (modInfos == null || modInfos.isEmpty()) {
                                        LOGGER.warn("Mod 信息为空,跳过上传");
                                        return;
                                    }

                                    HttpRequestUtil.postMultipart(CommonConstants.MOD_REGISTER, GSON.toJson(modInfos), files, PlatformType.INTERNAL);
                                    LOGGER.info("Mod 信息上传成功,共 {} 个 Mod", modInfos.size());
                                } catch (Exception e) {
                                    LOGGER.error("上传 Mod 信息失败", e);
                                }
                            }).exceptionally(e -> {
                                LOGGER.error("等待 Mod 采集完成时出现异常", e);
                                return null;
                            });

                } else {
                    LOGGER.warn("服务器注册失败,返回状态: {}", response);
                }
            }).exceptionally(e -> {
                LOGGER.error("服务器注册失败", e);
                return null;
            });

        } catch (Exception e) {
            LOGGER.error("构建服务器注册请求失败", e);
        }
    }

    private static ServerInfo buildFrom(MinecraftServer server) {
        ServerInfo info = new ServerInfo();

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

        info.setMinecraftVersion(server.getServerVersion());
        info.setCoreType(server.getServerModName());
        info.setCoreVersion(server.getServerVersion());
        String publicIp = HttpRequestUtil.getPublicIp();
        GlobalCache.setServerIp(publicIp);
        info.setServerIp(publicIp);
        info.setCurrentPlayers((long) server.getPlayerCount());
        info.setMaxPlayers((long) server.getMaxPlayers());
        info.setJavaVersion(System.getProperty("java.version"));

        return info;
    }

    private static int booleanConvertInteger(boolean b) {
        return b ? 1 : 0;
    }
}
