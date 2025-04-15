package cn.sanyeyun.constant;

/**
 * 常量
 *
 * @author 徐亚松
 * 2025-04-14 14:37
 **/
public class CommonConstants {
    public static final String HEADER_AUTHORIZATION = "MonitorAuth"; // 自定义头部字段
    public static final String BASE_URL = "http://localhost:8070";
    // 服务器注册 post
    public static final String SERVERS_REGISTER = "/minecraft/minecraftServers/register";

    // 服务器人数更新 put
    public static final String UPDATE_ONLINE_COUNT = "/minecraft/minecraftServers/updateOnlineCount";
    // 测试Url
    public static final String SERVERS_TEST = "/minecraft/minecraftServers/test";
}
