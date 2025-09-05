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
    public static final String HEADER_AUTHORIZATION = "monitorAuth"; // 自定义头部字段
    public static final String HEADER_SERVER_IP = "serverIp"; // 自定义头部字段
    public static final String HEADER_SERVER_PORT = "serverPort"; // 自定义头部字段
    // forge请求头
    public static final String HEADER_API_KEY = "x-api-key";
    public static final String CURSEFORGE_API_KEY = "$2a$10$oqwLSDrNDq032UOtKFHqvuAOisk4pRn4ZqDNOMp5UL6zQM7jDPLZW"; // 建议从配置中加载


    // 基础路径
    public static final String BASE_INTERNAL = "http://localhost:8070";
    //public static final String BASE_INTERNAL = "http://liebepj.cn";
    public static final String BASE_MODRINTH = "https://api.modrinth.com";
    public static final String BASE_CURSEFORGE = "https://api.curseforge.com";


    // 服务器注册 post
    public static final String SERVERS_REGISTER = "/minecraft/minecraftServers/register";
    // 服务器登出 put
    public static final String SERVERS_LOG_OUT = "/minecraft/minecraftServers/logOut";
    // 服务器人数更新 put
    public static final String UPDATE_ONLINE_COUNT = "/minecraft/minecraftServers/updateOnlineCount";
    // Mod注册 post
    public static final String MOD_REGISTER = "/minecraft/modInfo/register";
    // 测试Url
    public static final String SERVERS_TEST = "/minecraft/minecraftServers/test";


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


}
