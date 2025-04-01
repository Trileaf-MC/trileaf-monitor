package com.trileaf.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三叶云控统一面板属性
 *
 * @author 徐亚松
 * 2025/3/27 14:52
 */
@ConfigurationProperties(prefix = "trileaf.monitor")
@Data
@Slf4j
public class TrileafMonitorConfig {
    private Map<String, List<PanelConfig>> panel;

    @PostConstruct
    public void logConfig() {
        log.info("Loaded TrileafMonitorConfig: {}", JSONObject.toJSONString(panel));
    }
    /**
     * 统一面板属性类
     *
     * @author 徐亚松
     * 2025/3/27 14:24
     */
    @Data
    public static class PanelConfig {
        /**
         * 面板类型。
         */
        private String type;
        /**
         * 基础URL，用于构建完整的API请求地址。
         */
        private String baseUrl;
        /**
         * API密钥，用于验证API请求的身份。
         */
        private String apiKey;
        /**
         * 包含所有API路径的嵌套类。
         */
        private ApiPaths apiPaths;

        /**
         * 嵌套类用于存储不同API的路径。
         */
        @Data
        public static class ApiPaths {
            /**
             * 所有API请求的基础前缀。
             */
            private String prefix;

            /**
             * 获取概览信息的API路径。
             */
            private String overview;

            /**
             * 获取用户列表的API路径。
             */
            private String userList;

            /**
             * 获取实例列表的API路径。
             */
            private String instancesList;

            /**
             * 获取特定实例详情的API路径。
             */
            private String instanceDetail;

            /**
             * 获取文库列表的API路径。
             */
            private String fileList;

            /**
             * 获取文件内容的API路径。
             */
            private String fileContent;

            /**
             * 解压文件的API路径。
             */
            private String decompress;
        }
    }
}
