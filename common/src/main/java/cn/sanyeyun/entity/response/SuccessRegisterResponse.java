package cn.sanyeyun.entity.response;

import lombok.Data;

/**
 * 注册成功响应
 *
 * @author 徐亚松
 * 2025/9/10 10:46
 */
@Data
public class SuccessRegisterResponse {
    /**
     * 服务器Id
     */
    private Long serverId;
    /**
     * 验证码
     */
    private String verificationCode;
    /**
     * 验证码
     */
    private Boolean claim;
}
