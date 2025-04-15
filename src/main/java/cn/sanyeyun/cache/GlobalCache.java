package cn.sanyeyun.cache;

import lombok.Getter;
import lombok.Setter;

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
         * 服务器名称
         */
        private String serverName;
        /**
         * 密码
         */
        private String password;
        /**
         * QQ 群号
         */
        private String qqGroup;
        /**
         * 官方网站地址
         */
        private String officialWebsite;
        /**
         * 下载链接地址
         */
        private String downloadUrl;
        /**
         * 下载说明
         */
        private String downloadRemarks;
        /**
         * 授权 header 值
         */
        private String monitorAuth;
        /**
         * 是否对外开放
         */
        private Boolean isOpen;
    }


    // 私有构造方法
    private GlobalCache() {

    }
}
