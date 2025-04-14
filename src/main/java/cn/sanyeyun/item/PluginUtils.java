package cn.sanyeyun.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginUtils {
    private static final File PLUGINS_FOLDER = new File(System.getProperty("user.dir"), "plugins");

    // 获取插件核心和类型信息
    public static void getPluginInfo() {
        File pluginsDir = new File(String.valueOf(PLUGINS_FOLDER));
        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            System.out.println("[PluginReader] plugins 目录不存在");
            return;
        }

        File[] pluginFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (pluginFiles == null || pluginFiles.length == 0) {
            System.out.println("[PluginReader] 没有找到插件文件");
            return;
        }

        for (File pluginFile : pluginFiles) {
            try (JarFile jarFile = new JarFile(pluginFile)) {
                JarEntry pluginYml = jarFile.getJarEntry("plugin.yml");
                if (pluginYml != null) {
                    System.out.println("[PluginReader] 发现插件: " + pluginFile.getName());
                    // 读取插件核心信息
                    // 这里可以使用 YAML 解析库，如 SnakeYAML，来解析 plugin.yml 文件
                    System.out.println("[PluginReader] 核心插件: " + getPluginCoreInfo(jarFile));
                    System.out.println("[PluginReader] 插件类型: " + getPluginTypeInfo(jarFile));
                } else {
                    System.out.println("[PluginReader] 插件没有 plugin.yml 文件: " + pluginFile.getName());
                }
            } catch (IOException e) {
                System.err.println("[PluginReader] 读取插件失败: " + pluginFile.getName());
            }
        }
    }

    // 获取插件核心信息（例如：主要类）
    private static String getPluginCoreInfo(JarFile jarFile) {
        try {
            // 读取 plugin.yml 文件
            JarEntry pluginYml = jarFile.getJarEntry("plugin.yml");
            try (FileInputStream fis = new FileInputStream(new File(jarFile.getName(), pluginYml.getName()))) {
                // 使用 YAML 解析库读取 plugin.yml，获取核心插件类
                // 示例：这里简化处理，返回一个示例核心类
                return "core.plugin.Main";
            }
        } catch (IOException e) {
            return "无法读取核心信息";
        }
    }

    // 获取插件类型信息（例如：Minecraft 插件或其他类型的插件）
    private static String getPluginTypeInfo(JarFile jarFile) {
        try {
            // 读取 plugin.yml 文件，获取插件类型
            JarEntry pluginYml = jarFile.getJarEntry("plugin.yml");
            try (FileInputStream fis = new FileInputStream(new File(jarFile.getName(), pluginYml.getName()))) {
                // 使用 YAML 解析库读取 plugin.yml，获取插件类型
                // 示例：这里简化处理，返回一个示例插件类型
                return "Bukkit Plugin";
            }
        } catch (IOException e) {
            return "无法读取类型信息";
        }
    }
}
