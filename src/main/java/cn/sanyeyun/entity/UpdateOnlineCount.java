package cn.sanyeyun.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在线人数实体类
 *
 * @author 徐亚松
 * 2025-04-14 22:16
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOnlineCount {
    /**
     * IP地址
     */
    private String serverIp;
    /**
     * 端口
     */
    private Long serverPort;
    /**
     * 在线人数
     */
    private Long currentPlayers;
    /**
     * 最大人数
     */
    private Long maxPlayers;
}
