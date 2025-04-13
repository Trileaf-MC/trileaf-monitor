package cn.sanyeyun.trileafmonitormod.item;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class MyMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // 注册服务器启动事件监听
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
    }

    private void onServerStarted(MinecraftServer server) {
        // 获取服务器实例后，打印当前在线玩家数量
        int onlinePlayersCount = server.getCurrentPlayerCount();
        System.out.println("当前在线玩家数: " + onlinePlayersCount);
    }
}