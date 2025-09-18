package cn.sanyeyun.entity.response;

import lombok.Builder;
import lombok.Data;

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
    /**
     * 验证码
     */
    private String verificationCode;
    /**
     * 是否被认领
     */
    private Boolean claim;
}
