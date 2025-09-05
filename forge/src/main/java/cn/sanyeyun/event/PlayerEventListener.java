package cn.sanyeyun.event;

import cn.sanyeyun.TrileafMonitorForgeMod;
import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.entity.UpdateOnlineCount;
import cn.sanyeyun.enums.PlatformType;
import cn.sanyeyun.executor.NextTickExecutor;
import cn.sanyeyun.utils.HttpRequestUtil;
import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
@Mod.EventBusSubscriber(modid = TrileafMonitorForgeMod.MOD_ID)
public class PlayerEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerEventListener.class);

    public static void register() {
        // 在Forge中，使用@Mod.EventBusSubscriber注解后不需要手动注册
    }

    /**
     * 玩家登录事件处理
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TrileafMonitorForgeMod.LOGGER.info("玩家 {} 加入了游戏", player.getName().getString());
            NextTickExecutor.runNextTick(() -> {
                LOGGER.info("玩家加入{}", player.getName().getString());
                MinecraftServer server = player.getServer();
                if (server != null) {
                    updateOnlineCount(server);
                }
            }, 3);  // 延迟 3 Tick
        }
    }

    /**
     * 玩家登出事件处理
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TrileafMonitorForgeMod.LOGGER.info("玩家 {} 离开了游戏", player.getName().getString());
            NextTickExecutor.runNextTick(() -> {
                LOGGER.info("玩家离开{}", player.getName().getString());
                MinecraftServer server = player.getServer();
                if (server != null) {
                    updateOnlineCount(server);
                }
            }, 2);  // 延迟 2 Tick
        }
    }

    /**
     * 玩家使用物品事件 (目前被注释掉，保留转换后的代码)
     */
    /*
    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level world = event.getLevel();
        InteractionHand hand = event.getHand();

        // 获取玩家当前使用的物品
        ItemStack stack = player.getItemInHand(hand);
        ModService.collectModInfo();

        // 打印日志
        // LOGGER.info("玩家 {} 使用了物品：{}", player.getName().getString(), stack.getDisplayName().getString());

        // Forge无需返回值，事件可以通过setCanceled(true)取消
    }
    */

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
     * @return {@link UpdateOnlineCount}
     * @author 徐亚松
     * <p>2025/4/14 22:25</p>
     */
    private static UpdateOnlineCount buildFrom(MinecraftServer server) {
        UpdateOnlineCount onlineCount = new UpdateOnlineCount();
        onlineCount.setCurrentPlayers((long) server.getPlayerCount());
        onlineCount.setMaxPlayers((long) server.getMaxPlayers());
        onlineCount.setServerIp(GlobalCache.getServerIp());
        onlineCount.setServerPort(GlobalCache.getServerPort());
        return onlineCount;
    }
}
