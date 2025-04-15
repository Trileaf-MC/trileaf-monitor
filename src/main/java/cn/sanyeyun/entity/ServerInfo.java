package cn.sanyeyun.entity;

import lombok.Data;


/**
 * 服务器信息实体类
 *
 * @author 徐亚松
 * 2025-04-14 10:58
 **/
@Data
public class ServerInfo {
    /** 服务器名称 */
    private String serverName;
    /** IP地址 */
    private String serverIp;
    /** 端口 */
    private Long serverPort;
    /** 图标 */
    private String icon;
    /** 游戏模式 (survival:生存 creative:创造 adventure:冒险 spectator:旁观) */
    private String gameMode;
    /** 游戏难度 (peaceful:和平模式 easy:简单模式 normal:正常模式 hard:困难模式) */
    private String difficulty;
    /** 在线人数 */
    private Long currentPlayers;
    /** 最大人数 */
    private Long maxPlayers;
    /** 描述 */
    private String motd;
    /** 游戏版本 */
    private String minecraftVersion;
    /** QQ群 */
    private String qqGroup;
    /** 官网地址 */
    private String officialWebsite;
    /** 访问密码 */
    private String password;
    /** 下载地址 */
    private String downloadUrl;
    /** 下载备注 */
    private String downloadRemarks;
    /** PVP模式(0: 否, 1: 是) */
    private Integer isPvp;
    /** 正版验证(0: 否, 1: 是) */
    private Integer isOnlineMode;
    /** 白名单(0: 否, 1: 是) */
    private Integer isEnforceWhitelist;
    /** SLP协议(0: 否, 1: 是) */
    private Integer hasEnableQuery;
    /** 是否对外开放(0: 否, 1: 是) */
    private Integer isOpen;
    /** 核心类型 */
    private String coreType;
    /** 核心版本号 */
    private String coreVersion;
}
