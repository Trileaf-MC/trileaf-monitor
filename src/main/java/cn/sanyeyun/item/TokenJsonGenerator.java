package cn.sanyeyun.item;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenJsonGenerator {

    // 生成随机 Base64 字符串（32 字节）
    public static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];  // 32 字节的随机数据
        random.nextBytes(bytes);  // 填充随机字节
        return Base64.getEncoder().encodeToString(bytes);  // 转为 Base64 字符串
    }

    // 创建并写入 token.json 文件
    public static void createTokenJsonFile(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("authorization", "Bearer " + token);

        // 获取 config 目录
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();  // 如果目录不存在，创建它
        }

        // 创建 token.json 文件并写入 JSON 数据
        File tokenFile = new File(configDir, "TrilefCertification.json");
        try (FileWriter writer = new FileWriter(tokenFile)) {
            writer.write(jsonObject.toString());
            System.out.println("token.json 文件已成功创建！");
        } catch (IOException e) {
            System.err.println("创建 token.json 文件时出错：" + e.getMessage());
        }
    }


}
