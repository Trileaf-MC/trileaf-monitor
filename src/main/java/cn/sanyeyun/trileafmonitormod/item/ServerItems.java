package cn.sanyeyun.trileafmonitormod.item;

import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class ServerItems {
    public static JsonObject getServerInfoJson() {
        JsonObject json = new JsonObject();

        // 获取服务器类型
        String serverType = determineServerType();

        try (InputStream input = new FileInputStream("server.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            // 获取配置项
            String serverName = prop.getProperty("server-name", "未知服务器");
            String ipAddress = getPublicIp();
            String port = prop.getProperty("server-port", "25565");
            String motd = prop.getProperty("motd", "欢迎加入服务器");
            boolean onlineMode = Boolean.parseBoolean(prop.getProperty("online-mode", "true"));
            boolean whiteList = Boolean.parseBoolean(prop.getProperty("white-list", "false"));
            String gamedown = prop.getProperty("gamemode", "0");
            String diffculty = prop.getProperty("diffculty", "正常");

            // 获取并添加到 JSON
            json.addProperty("服务器名称", serverName);
            json.addProperty("IP地址", ipAddress);
            json.addProperty("端口号", port);
            json.addProperty("MOTD", motd);
            json.addProperty("是否开启正版验证", onlineMode);
            json.addProperty("是否开启白名单", whiteList);
            json.addProperty("服务器类型", serverType);
            json.addProperty("游戏模式", gamedown);
            json.addProperty("难度", diffculty);

            // 创建 MinecraftServer 对象并获取服务器信息
            MinecraftServer minecraftServer = new MinecraftServer(ipAddress, Integer.parseInt(port));
            JsonObject serverData = minecraftServer.fetchServerData();

            if (serverData.has("error")) {
                json.addProperty("error", serverData.get("error").getAsString());
            } else {
                json.add("服务器数据", serverData);
            }

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

    public static String getPublicIp() {
        String[] apis = {
                "https://api.ipify.org?format=text", // ipify
                "https://ipinfo.io/ip",             // ipinfo.io
                "https://icanhazip.com",            // icanhazip.com
                "https://api.myip.com"             // myip.com
        };

        for (String apiUrl : apis) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String ip = reader.readLine();
                reader.close();

                if (ip != null && !ip.isEmpty()) {
                    return ip;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "无法获取公网 IP";
    }
}
