package cn.sanyeyun.event;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.entity.ServerInfo;
import cn.sanyeyun.entity.UpdateOnlineCount;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.executor.NextTickExecutor;
import cn.sanyeyun.utils.HttpRequestUtil;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static cn.sanyeyun.constant.CommonConstants.UPDATE_ONLINE_COUNT;

/**
 * 玩家变化监听器
 *
 * @author 徐亚松
 * 2025-04-14 14:18
 **/
public class PlayerEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerEventListener.class);

    public static void register() {
        // 玩家连接
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            NextTickExecutor.runNextTick(() -> {
                LOGGER.info("玩家加入{}", player.getEntityName());
                updateOnlineCount(server);
            }, 3);  // 延迟 3 Tick


            ServerEventListener.register();
        });

        // 玩家离线
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            NextTickExecutor.runNextTick(() -> {
                LOGGER.info("玩家离开{}", player.getEntityName());
                updateOnlineCount(server);
            }, 2);  // 延迟 2 Tick
        });

   /*     UseItemCallback.EVENT.register((player, world, hand) -> {
            // 获取玩家当前使用的物品
            ItemStack stack = player.getStackInHand(hand);
            ModService.collectModInfo();

            // 打印日志
            // LOGGER.info("玩家 {} 使用了物品：{}", player.getEntityName(), stack.getItem().getName().getString());

            // 返回 TypedActionResult.pass(stack) 以便事件继续执行并返回默认行为
            return TypedActionResult.pass(stack);
        });*/
    }

    /**
     * 更新在线人数
     *
     * @param server 我的世界实例
     * @author 徐亚松
     * <p>2025/4/15 09:37</p>
     */
    private static void updateOnlineCount(MinecraftServer server) {
        // 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequestUtil.put(UPDATE_ONLINE_COUNT, new Gson().toJson(buildFrom(server)), PlatformType.INTERNAL);
            } catch (Exception e) {
                LOGGER.error("在线人数上传失败", e);
            }
        });
    }

    /**
     * 构建参数
     *
     * @param server 实例信息
     * @return {@link ServerInfo}
     * @author 徐亚松
     * <p>2025/4/14 22:25</p>
     */
    private static UpdateOnlineCount buildFrom(MinecraftServer server) {
        UpdateOnlineCount onlineCount = new UpdateOnlineCount();
        onlineCount.setCurrentPlayers((long) server.getCurrentPlayerCount());
        onlineCount.setMaxPlayers((long) server.getMaxPlayerCount());
        onlineCount.setServerIp(GlobalCache.getServerRuntimeInfo().getServerIp());
        onlineCount.setServerPort(GlobalCache.getServerRuntimeInfo().getServerPort());
        return onlineCount;
    }

}
