package cn.sanyeyun.entity;

import lombok.Data;

@Data
public class ServerInfo {
    private Long serverPort;
    private String gameMode;
    private String difficulty;
    private Integer isPvp;
    private Integer isOnlineMode;
    private Integer isEnforceWhitelist;
    private Integer hasEnableQuery;
    private String motd;
    private String minecraftVersion;
    private String coreType;
    private String coreVersion;
    private String serverIp;
    private Long currentPlayers;
    private Long maxPlayers;
    private String javaVersion;
    // private String icon; // If icon is needed later, uncomment and add field/setter
} 