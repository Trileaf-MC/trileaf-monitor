package cn.sanyeyun.event;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.constant.CommonConstants;
import cn.sanyeyun.entity.ModInfo;
import cn.sanyeyun.entity.ServerInfo;
import cn.sanyeyun.entity.response.RuoYiResponse;
import cn.sanyeyun.entity.response.SuccessLoginResponse;
import cn.sanyeyun.entity.response.SuccessRegisterResponse;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.service.ModService;
import cn.sanyeyun.utils.ConfigFileManager;
import cn.sanyeyun.utils.HttpRequestUtil;
import cn.sanyeyun.utils.KeyPairUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.sanyeyun.constant.CommonConstants.SERVERS_LOGIN;
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
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();


    public static void register() {
        // 游戏启动中，异步收集 Mod 信息
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LOGGER.info("服务端启动中，准备获取 Mod 信息");
            ModService.collectModInfoAsync();
        });

        // 游戏关闭时异步通知退出
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LOGGER.info("服务端关闭中");
            CompletableFuture.runAsync(() -> HttpRequestUtil.put(CommonConstants.SERVERS_LOG_OUT, null, PlatformType.INTERNAL));
        });

        // 游戏启动完成后注册服务器
        ServerLifecycleEvents.SERVER_STARTED.register(ServerEventListener::handleServerStarted);
    }

    /**
     * 注册服务器信息
     *
     * @param server 服务器实例
     * @author 徐亚松
     * <p>2025/5/7 21:07</p>
     */
    private static void handleServerStarted(MinecraftServer server) {
        String serverId = GlobalCache.getTrileafCertification().getServerId();
        // 已注册，直接登录
        if (serverId != null && !serverId.isEmpty()) {
            serviceRegisterSuccess(serverId);
            return;
        }

        ServerInfo info = buildFrom(server);
        String json = GSON.toJson(info);

        // 异步注册服务器
        CompletableFuture<RuoYiResponse> registerFuture = CompletableFuture.supplyAsync(() ->
                GSON.fromJson(HttpRequestUtil.post(SERVERS_REGISTER, json, PlatformType.INTERNAL), RuoYiResponse.class)
        );

        // 等待注册完成 & Mod 信息收集完成，再上传 Mod
        CompletableFuture<Void> modFuture = CompletableFuture.allOf(GlobalCache.getModInfos(), GlobalCache.getCompletelyUnmatchedFiles());

        // 注册成功后处理登录/签名
        registerFuture.thenAccept(response -> {
            if (response != null && response.isSuccess()) {
                LOGGER.info("服务器注册成功，登录并准备登录并上传 Mod 信息");
                SuccessRegisterResponse obj = GSON.fromJson(GSON.toJson(response.getData()), SuccessRegisterResponse.class);
                serviceRegisterSuccess(obj.getServerId());
                // 等待 Mod 信息收集完成，再上传
                modFuture.thenRun(ServerEventListener::uploadModInfos);
            } else {
                LOGGER.warn("服务器注册失败，返回状态: {}", response);
            }
        }).exceptionally(e -> {
            LOGGER.error("服务器注册失败", e);
            return null;
        });
    }

    /**
     * 登录 签名逻辑
     *
     * @param serverId 服务器Id
     * @author 徐亚松 2025/9/10 11:46
     */
    private static void serviceRegisterSuccess(String serverId) {
        GlobalCache.getTrileafCertification().setServerId(serverId);
        ConfigFileManager.saveConfig();

        try {
            String signature = KeyPairUtil.sign(serverId, GlobalCache.getKeyPair().getPrivate());
            JsonObject loginRequest = new JsonObject();
            loginRequest.addProperty("serverId", serverId);
            loginRequest.addProperty("signature", signature);

            // 调用登录接口并解析返回值
            RuoYiResponse ruoYiResponse = GSON.fromJson(HttpRequestUtil.post(SERVERS_LOGIN, GSON.toJson(loginRequest), PlatformType.INTERNAL), RuoYiResponse.class);
            SuccessLoginResponse result = GSON.fromJson(GSON.toJson(ruoYiResponse.getData()), SuccessLoginResponse.class);
            GlobalCache.setAuthorization(result.getAuthorization());
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException("登录签名异常", e);
        }
    }


    /**
     * 上传 Mod 信息
     *
     * @author 徐亚松 2025/9/15 16:07
     */
    private static void uploadModInfos() {
        try {
            List<ModInfo> modInfos = GlobalCache.getModInfos().join();
            List<File> files = GlobalCache.getCompletelyUnmatchedFiles().join();

            if (modInfos == null || modInfos.isEmpty()) {
                LOGGER.warn("Mod 信息为空，跳过上传");
                return;
            }

            HttpRequestUtil.postMultipart(CommonConstants.MOD_REGISTER, GSON.toJson(modInfos), files, PlatformType.INTERNAL);
            LOGGER.info("Mod 信息上传成功，共 {} 个 Mod", modInfos.size());
        } catch (Exception e) {
            LOGGER.error("上传 Mod 信息失败", e);
        }
    }


    /**
     * 每 1 秒轮询一次 /isClaim，直到返回 true 或者超时
     */
    private static void startClaimPolling(String verificationCode) {
        final int maxAttempts = 60; // 最多 60 次，大约 1 分钟
        final int[] attempts = {0};

        SCHEDULER.scheduleAtFixedRate(() -> {
            try {
                Map<String, Object> params = Map.of("verificationCode", verificationCode);
                String resp = HttpRequestUtil.get(CommonConstants.IS_CLAIM, params, PlatformType.INTERNAL);

                // 反序列化成 AjaxResult
                RuoYiResponse result = GSON.fromJson(resp, RuoYiResponse.class);

                if (result != null && result.isSuccess()) {
                    Object data = result.getData();
                    if (data instanceof Boolean claimed && claimed) {
                        LOGGER.info("验证码 {} 已被认领 ✅", verificationCode);
                        SCHEDULER.shutdown();
                        return;
                    }
                }

                attempts[0]++;
                if (attempts[0] >= maxAttempts) {
                    LOGGER.warn("验证码 {} 在 {} 秒内未被认领，停止轮询 ⏹️", verificationCode, maxAttempts);
                    SCHEDULER.shutdown();
                }
            } catch (Exception e) {
                LOGGER.error("轮询 isClaim 接口失败", e);
            }
        }, 0, 1, TimeUnit.SECONDS);
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

        String publicKeyStr = Base64.getEncoder()
                .encodeToString(GlobalCache.getKeyPair().getPublic().getEncoded());
        info.setPublicKey(publicKeyStr);

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
