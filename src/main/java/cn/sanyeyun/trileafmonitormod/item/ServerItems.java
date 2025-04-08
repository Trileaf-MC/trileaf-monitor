package cn.sanyeyun.trileafmonitormod.item;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerItems {

    // 获取服务器基本信息并返回 JSON 对象
    public static JsonObject getServerInfoJson() {
        JsonObject json = new JsonObject();

        // 获取服务器类型
        String serverType = determineServerType();

        try (InputStream input = new FileInputStream("server.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            // 获取配置项
            String serverName = prop.getProperty("server-name", "未知服务器");
            String ipAddress = prop.getProperty("server-ip", "0.0.0.0");
            String port = prop.getProperty("server-port", "25565");
            String motd = prop.getProperty("motd", "欢迎加入服务器");
            boolean onlineMode = Boolean.parseBoolean(prop.getProperty("online-mode", "true"));
            boolean whiteList = Boolean.parseBoolean(prop.getProperty("white-list", "false"));

            // 添加到 JSON
            json.addProperty("服务器名称", serverName);
            json.addProperty("IP地址", ipAddress);
            json.addProperty("端口号", port);
            json.addProperty("MOTD", motd);
            json.addProperty("是否开启正版验证", onlineMode);
            json.addProperty("是否开启白名单", whiteList);
            json.addProperty("服务器类型", serverType);

            // 获取服务器版本
            String version = getServerVersion();
            json.addProperty("游戏版本", version);

        } catch (IOException e) {
            json.addProperty("error", "读取 server.properties 失败：" + e.getMessage());
        }

        return json;
    }

    // 判断服务器类型
    public static String determineServerType() {
        File modsFolder = new File("mods");
        File pluginsFolder = new File("plugins");

        boolean hasMods = modsFolder.exists() && modsFolder.isDirectory();
        boolean hasPlugins = pluginsFolder.exists() && pluginsFolder.isDirectory();

        if (hasMods && hasPlugins) {
            return "混合服";
        } else if (hasMods) {
            return "模组服";
        } else if (hasPlugins) {
            return "插件服";
        } else {
            return "纯净服";
        }
    }

    // 获取服务器版本
    public static String getServerVersion() {
        // 示例：假设 server.jar 的文件名为 "minecraft_server.1.20.4.jar"
        File serverJar = new File("server.jar");
        if (serverJar.exists()) {
            String fileName = serverJar.getName();
            int start = fileName.indexOf("minecraft_server.") + "minecraft_server.".length();
            int end = fileName.lastIndexOf(".jar");
            if (start >= 0 && end > start) {
                return fileName.substring(start, end);
            }
        }
        return "未知版本";
    }
}
