package cn.sanyeyun.trileafmonitormod.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

public class ServerItems {

    private static final String SERVER_URL = "https://your-server.com/api/hash"; // 你的目标服务器地址

    // 获取服务器信息的 JSON 数据
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
            String difficulty = prop.getProperty("diffculty", "正常");

            // 获取并添加到 JSON
            json.addProperty("服务器名称", serverName);
            json.addProperty("IP地址", ipAddress);
            json.addProperty("端口号", port);
            json.addProperty("MOTD", motd);
            json.addProperty("是否开启正版验证", onlineMode);
            json.addProperty("是否开启白名单", whiteList);
            json.addProperty("服务器类型", serverType);
            json.addProperty("游戏模式", gamedown);
            json.addProperty("难度", difficulty);
            json.addProperty("游戏版本", getServerVersion());

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

    // 获取公网 IP 地址
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

    // 发送服务器信息的 JSON 数据到目标服务器
    public static void sendServerInfoToServer(JsonObject serverInfoJson) {
        if (serverInfoJson == null) return;

        // 将服务器信息转换为 JSON 字符串
        String jsonStr = serverInfoJson.toString();

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // 获取Authorization Token
            String token = getAuthorizationToken();
            if (token != null) {
                // 设置请求头
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", token);  // 使用从JSON中获取的 token
            } else {
                System.err.println("未能读取到Token，发送请求时将无法进行身份验证！");
            }

            // 启用输出流以发送请求体
            connection.setDoOutput(true);

            // 发送请求体数据
            connection.getOutputStream().write(jsonStr.getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();

            // 获取服务器响应
            int responseCode = connection.getResponseCode();
            System.out.println("服务器响应码:" + responseCode);
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    while (scanner.hasNextLine()) {
                        System.out.println(" 服务器响应: " + scanner.nextLine());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("发送失败");
            e.printStackTrace();
        }
    }

    // 从 mod.json 文件中获取 Authorization Token
    private static String getAuthorizationToken() {
        try (FileReader reader = new FileReader("config/TrilefCertification.json")) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.get("authorization").getAsString();  // 从 JSON 中获取 "authorization" 字段
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // 如果读取失败，则返回 null
        }
    }
}
