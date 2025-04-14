package cn.sanyeyun.item;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * @author 徐亚松
 * 2025-04-05 09:04
 **/
public class ModItems {
    private static final File MODS_FOLDER = new File(System.getProperty("user.dir"), "mods");

    private static final String SERVER_URL = "https://your-server.com/api/hash"; // 替换为你的服务器地址

    public static void registerModItems() {
        // ModItemRegistry.registerItems();
        System.out.println("[HashSenderMod] Mod Initialized");
        Map<String, String> modHashes = getModHashes();
        sendHashesToServer(modHashes);
    }



    private static Map<String, String> getModHashes() {
        Map<String, String> hashMap = new HashMap<>();
        File modsDir = new File(String.valueOf(MODS_FOLDER));
        if (!modsDir.exists() || !modsDir.isDirectory()) {
            System.out.println("[HashSenderMod] mods 目录不存在");
            return hashMap;
        }

        File[] files = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return hashMap;

        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file)) {
                String hash = DigestUtils.sha1Hex(fis);
                hashMap.put(file.getName(), hash);
                System.out.println("[HashSenderMod] 读取模组: " + file.getName() + " -> " + hash);
            } catch (IOException e) {
                System.err.println("[HashSenderMod] 计算哈希失败: " + file.getName());
            }
        }
        return hashMap;
    }

    private static void sendHashesToServer(Map<String, String> hashes) {
        if (hashes.isEmpty()) return;

        StringBuilder jsonBuilder = new StringBuilder("{");
        for (Map.Entry<String, String> entry : hashes.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\",");
        }
        jsonBuilder.setLength(jsonBuilder.length() - 1); // 移除最后的逗号
        jsonBuilder.append("}");

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonBuilder.toString().getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();

            int responseCode = connection.getResponseCode();
            System.out.println("[HashSenderMod] 服务器响应码: " + responseCode);
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    while (scanner.hasNextLine()) {
                        System.out.println("[HashSenderMod] 服务器响应: " + scanner.nextLine());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[HashSenderMod] 发送哈希数据失败");
        }
    }
}
