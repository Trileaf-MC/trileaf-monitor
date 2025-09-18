package cn.sanyeyun.cache;

import cn.sanyeyun.entity.ModInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 全局缓存类
 * 用于存储运行时使用的缓存数据，如配置项、服务器信息等
 *
 * @author 徐亚松
 * 2025-04-14 11:43
 **/
public class GlobalCache {
    /**
     * IP地址
     */
    @Getter
    @Setter
    private static String serverIp;

    /**
     * 端口
     */
    @Getter
    @Setter
    private static Long serverPort;

    /**
     * Mod信息 修改成异步方式
     */
    @Getter
    private static final CompletableFuture<List<ModInfo>> modInfos = new CompletableFuture<>();

    /**
     * Mod文件信息 修改成异步方式
     */
    @Getter
    private static final CompletableFuture<List<File>> completelyUnmatchedFiles = new CompletableFuture<>();

    /**
     * 配置文件映射对象
     */
    @Getter
    @Setter
    private static TrileafCertification trileafCertification;

    /**
     * 密钥对象
     */
    @Getter
    @Setter
    private static KeyPair keyPair;

    /**
     * token
     */
    @Getter
    @Setter
    private static String authorization;

    /**
     * 验证码
     */
    @Getter
    @Setter
    private static String verificationCode;

    /**
     * 配置文件
     */
    public static class TrileafCertification {
        /**
         * 授权 header 值
         */
        //private String monitorAuth;

        /**
         * 服务器Id
         */
        @Getter
        @Setter
        private String serverId;


        /**
         * 是否被认领
         */
        @Getter
        @Setter
        private Boolean claim = false;

    }


    // 私有构造方法
    private GlobalCache() {

    }
}
