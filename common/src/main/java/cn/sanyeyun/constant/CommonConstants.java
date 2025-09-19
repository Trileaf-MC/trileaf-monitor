package cn.sanyeyun.constant;

import lombok.Data;

/**
 * 常量
 *
 * @author 徐亚松
 * 2025-04-14 14:37
 **/
@Data
public class CommonConstants {
    // 后台请求头
    public static final String HEADER_SERVER_ID = "serverId"; // 自定义头部字段
    public static final String HEADER_SERVER_IP = "serverIp"; // 自定义头部字段
    public static final String HEADER_SERVER_PORT = "serverPort"; // 自定义头部字段
    // forge请求头
    public static final String HEADER_API_KEY = "x-api-key";
    public static final String CURSEFORGE_API_KEY = "$2a$10$oqwLSDrNDq032UOtKFHqvuAOisk4pRn4ZqDNOMp5UL6zQM7jDPLZW"; // 建议从配置中加载


    // 基础路径
    public static final String BASE_INTERNAL = "http://localhost:8070";
    //public static final String BASE_INTERNAL = "https://backend.oksanye.com/prod-api";
    public static final String BASE_MODRINTH = "https://api.modrinth.com";
    public static final String BASE_CURSEFORGE = "https://api.curseforge.com";


    // 服务器注册 post
    public static final String SERVERS_REGISTER = "/monitor/minecraftServers/register";
    // 服务器登出 put
    public static final String SERVERS_LOG_OUT = "/monitor/minecraftServers/logOut";
    // 服务器登录 post
    public static final String SERVERS_LOGIN = "/monitor/minecraftServers/login";
    // 服务器人数更新 put
    public static final String UPDATE_ONLINE_COUNT = "/monitor/minecraftServers/updateOnlineCount";
    // 服务器是否被认领 post
    public static final String IS_CLAIM = "/monitor/minecraftServers/isClaim";
    // 服务器心跳 post
    public static final String HEARTBEAT = "/monitor/minecraftServers/heartbeat";


    // Mod注册 post
    public static final String MOD_REGISTER = "/monitor/minecraftServers/registerMod";
    // 测试Url
    public static final String SERVERS_TEST = "/monitor/minecraftServers/test";


    // modrinth Api
    // 查询Mod信息
    public static final String VERSION_FILES = "/v2/version_files";
    // 查询项目
    public static final String PROJECTS = "/v2/projects";

    // forge Api
    // 查询Mod信息
    public static final String FINGERPRINTS = "/v1/fingerprints/432";
    // 查询项目
    public static final String MODS = "/v1/mods";


    // 查询项目
    public static final String AUTHORIZATION = "Authorization";

    // 查询项目
    public static final String BEARER = "Bearer ";


}
