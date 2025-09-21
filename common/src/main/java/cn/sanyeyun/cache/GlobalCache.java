package cn.sanyeyun.cache;

import cn.sanyeyun.entity.ModInfo;
import cn.sanyeyun.entity.response.SuccessLoginResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.security.KeyPair;
import java.util.List;
import java.util.Map;
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
     * 服务器运行时信息
     */
    @Getter
    @Setter
    private static ServerRuntimeInfo serverRuntimeInfo = new ServerRuntimeInfo();

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
     * 配置文件
     */
    @Getter
    @Setter
    public static class TrileafCertification {
        /**
         * 服务器Id
         */
        private Long serverId;
        /**
         * 是否被认领
         */
        private Boolean claim = false;

    }


    /**
     * 服务器运行时信息
     */
    @Getter
    @Setter
    public static class ServerRuntimeInfo {
        /**
         * 服务器 ip
         */
        private String serverIp;
        /**
         * 服务器 端口
         */
        private Long serverPort;
        /**
         * token
         */
        private String authorization;
        /**
         * 服务器 ID
         */
        private Long serverId;
        /**
         * 登录时间，毫秒时间戳
         */
        private Long loginTime;
        /**
         * 上一次心跳时间，毫秒时间戳
         */
        private Long lastHeartbeatTime;
        /**
         * 验证码过期时间 毫秒时间戳
         */
        private Long verificationExpireTime;
        /**
         * 验证码
         */
        private String verificationCode;
        /**
         * 是否被认领
         */
        private Boolean claim;
        /**
         * 额外信息，可存 IP、端口、版本等
         */
        private Map<String, Object> extraInfo;

        public void mergeFromResponse(SuccessLoginResponse other) {
            if (other == null) return;

            // 返回值有的就覆盖
            if (other.getAuthorization() != null) this.authorization = other.getAuthorization();
            if (other.getLoginTime() != null) this.loginTime = other.getLoginTime();
            if (other.getLastHeartbeatTime() != null) this.lastHeartbeatTime = other.getLastHeartbeatTime();
            if (other.getVerificationExpireTime() != null) this.verificationExpireTime = other.getVerificationExpireTime();
            if (other.getVerificationCode() != null) this.verificationCode = other.getVerificationCode();
            if (other.getClaim() != null) this.claim = other.getClaim();

            // extraInfo 返回的有就覆盖或添加
            if (other.getExtraInfo() != null) {
                if (this.extraInfo == null) this.extraInfo = new java.util.HashMap<>();
                this.extraInfo.putAll(other.getExtraInfo());
            }
        }


    }


    // 私有构造方法
    private GlobalCache() {

    }
}
