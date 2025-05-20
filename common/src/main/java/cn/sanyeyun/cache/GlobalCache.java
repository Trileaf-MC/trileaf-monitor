package cn.sanyeyun.cache;

import cn.sanyeyun.entity.ModInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

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
     * Mod信息
     */
    @Getter
    @Setter
    private static List<ModInfo> modInfos;

    /**
     * Mod文件信息
     */
    @Getter
    @Setter
    private static List<File> completelyUnmatchedFiles;

    /**
     * 配置文件映射对象
     */
    @Getter
    @Setter
    private static TrileafCertification trileafCertification;

    /**
     * 配置文件
     */
    @Getter
    @Setter
    public static class TrileafCertification {
        /**
         * 授权 header 值
         */
        private String monitorAuth;
    }


    // 私有构造方法
    private GlobalCache() {

    }
}
