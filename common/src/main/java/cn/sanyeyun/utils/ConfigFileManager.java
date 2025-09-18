package cn.sanyeyun.utils;

import cn.sanyeyun.cache.GlobalCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 配置文件管理工具类
 * 用于生成和管理 TrileafCertification.json 配置文件
 *
 * @author 徐亚松
 * 2025-04-14 11:34
 **/
public class ConfigFileManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileManager.class);

    private static final String CONFIG_DIR = "config";
    private static final String FILE_NAME = "TrileafCertification.json";
    private static final Path CONFIG_PATH = Path.of(CONFIG_DIR, FILE_NAME);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // 缓存配置数据

    /**
     * 加载配置文件
     *
     * @author 徐亚松
     * <p>2025/4/14 16:56</p>
     */
    public static void loader() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                GlobalCache.TrileafCertification config = gson.fromJson(reader, GlobalCache.TrileafCertification.class);
                GlobalCache.setTrileafCertification(config);
                LOGGER.info("成功加载配置文件：{}", FILE_NAME);
            } catch (IOException e) {
                LOGGER.error("加载配置文件时出错：", e);
            }
        } else {
            createConfigFile();
        }
    }

    /**
     * 创建配置文件并添加默认字段
     *
     * @author 徐亚松
     * <p>2025/4/14 17:03</p>
     */
    public static void createConfigFile() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            GlobalCache.TrileafCertification config = new GlobalCache.TrileafCertification();
            GlobalCache.setTrileafCertification(config);
            // 一次写入 避免重复io
            saveConfig();
            LOGGER.info("配置文件已创建并写入默认字段。");
        } catch (IOException e) {
            LOGGER.error("创建配置文件失败：", e);
        }
    }

    /**
     * 将缓存写入磁盘
     */
    public static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            gson.toJson(GlobalCache.getTrileafCertification(), writer);
            LOGGER.info("配置文件已保存到磁盘。");
        } catch (IOException e) {
            LOGGER.error("保存配置文件失败：", e);
        }
    }

}
