package cn.sanyeyun.utils;

import cn.sanyeyun.cache.GlobalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import static cn.sanyeyun.constant.CommonConstants.BASE_URL;
import static cn.sanyeyun.constant.CommonConstants.HEADER_AUTHORIZATION;


/**
 * Http工具类
 *
 * @author 徐亚松
 * 2025-04-14 14:29
 **/
public class HttpRequestUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    /**
     * POST 请求
     *
     * @param path 请求路径
     * @param json 请求体（JSON 格式字符串）
     * @return {@link String}
     * @author 徐亚松
     * <p>2025/4/14 22:11</p>
     */
    public static String post(String path, String json) {
        return sendRequest("POST", path, json);
    }

    /**
     * PUT 请求
     *
     * @param path 请求路径
     * @param json 请求体（JSON 格式字符串）
     * @return {@link String}
     * @author 徐亚松
     * <p>2025/4/14 22:11</p>
     */
    public static String put(String path, String json) {
        return sendRequest("PUT", path, json);
    }


    /**
     * 发送 HTTP 请求，返回响应内容字符串
     *
     * @param method 请求方法（POST/PUT）
     * @param path   接口路径（不包含 baseUrl）
     * @param json   请求体（JSON 格式字符串）
     * @return {@link String}
     * @author 徐亚松
     * <p>2025/4/14 22:12</p>
     */
    private static String sendRequest(String method, String path, String json) {
        String fullUrl = BASE_URL + path;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl)).header("Content-Type", "application/json")
                .header(HEADER_AUTHORIZATION, GlobalCache.getTrileafCertification().getMonitorAuth())
                .timeout(Duration.ofSeconds(10));

        // 根据传入的 HTTP 方法类型设置请求
        if ("POST".equalsIgnoreCase(method)) {
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json));
        } else if ("PUT".equalsIgnoreCase(method)) {
            requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            LOGGER.info("请求成功，URL: {}, 状态码: {}, 响应内容: {}", fullUrl, statusCode, body);
            return body;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("请求失败，URL: {}", fullUrl, e);
            return null;
        }
    }


    /**
     * 获取公网 IP 地址
     */
    public static String getPublicIp() {
        String[] apis = {"https://api.ipify.org", "https://ipinfo.io/ip", "https://icanhazip.com"};

        for (String apiUrl : apis) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).timeout(Duration.ofSeconds(5)).GET().build();
            try {
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    String ip = Optional.ofNullable(response.body()).orElse("").trim();
                    if (!ip.isEmpty()) {
                        return ip;
                    }
                } else {
                    LOGGER.warn("请求 {} 返回非 200 状态码: {}", apiUrl, response.statusCode());
                }
            } catch (Exception e) {
                LOGGER.error("请求失败 {}: {}", apiUrl, e.getMessage());
            }
        }

        return "无法获取公网 IP";
    }

}
