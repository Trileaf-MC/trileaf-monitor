package cn.sanyeyun.entity.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 登录成功响应
 * @author 徐亚松
 * 2025/9/15 16:09
 */
@Data
@Builder
public class SuccessLoginResponse{
    /**
     * token
     */
    private String authorization;
    /** 登录时间，毫秒时间戳 */
    private Long loginTime;
    /** 上一次心跳时间，毫秒时间戳 */
    private Long lastHeartbeatTime;
    /** token 到期时间，毫秒时间戳 */
    private Long expiresAt;
    /**
     * 验证码
     */
    private String verificationCode;
    /**
     * 验证码过期时间 毫秒时间戳
     */
    private Long verificationExpireTime;
    /**
     * 是否被认领
     */
    private Boolean claim;
    /** 额外信息，可存 IP、端口、版本等 */
    private Map<String, Object> extraInfo;
}
