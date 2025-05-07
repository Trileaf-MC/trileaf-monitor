package cn.sanyeyun.utils;

import cn.sanyeyun.cache.GlobalCache;
import cn.sanyeyun.enums.PlatformType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.sanyeyun.constant.CommonConstants.*;


/**
 * Http工具类
 *
 * @author 徐亚松
 * 2025-04-14 14:29
 **/
public class HttpRequestUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    /**
     * POST 请求
     *
     * @param path 请求路径
     * @param json 请求体（JSON 格式字符串）
     * @return {@link String}
     * @author 徐亚松
     * <p>2025/4/14 22:11</p>
     */
    public static String post(String path, String json, PlatformType platform) {
        return sendRequest("POST", path, json, platform);
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
    public static String put(String path, String json, PlatformType platform) {
        return sendRequest("PUT", path, json, platform);
    }

    /**
     * GET 请求（支持参数拼接）
     *
     * @param path     请求路径（不含 baseUrl）
     * @param params   请求参数（key-value 形式）
     * @param platform 平台类型（用于拼接 baseUrl 和添加请求头）
     * @author 徐亚松
     * <p>2025/5/6 15:30</p>
     */
    public static String get(String path, Map<String, Object> params, PlatformType platform) {
        String fullUrl = switch (platform) {
            case INTERNAL -> BASE_INTERNAL + path;
            case MODRINTH -> BASE_MODRINTH + path;
            case CURSEFORGE -> BASE_CURSEFORGE + path;
        };

        // 将 Map 的参数处理为查询字符串
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(prepareQueryParam(entry.getValue()), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        fullUrl += "?" + query;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(10));

        // 添加平台对应的请求头
        switch (platform) {
            case INTERNAL ->
                    requestBuilder.header(HEADER_AUTHORIZATION, GlobalCache.getTrileafCertification().getMonitorAuth());
            case CURSEFORGE -> requestBuilder.header(HEADER_API_KEY, CURSEFORGE_API_KEY);
            case MODRINTH -> {
                // 不需要额外请求头
            }
        }
        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            requestOk(platform, fullUrl, statusCode, body);
            return body;
        } catch (IOException | InterruptedException e) {
            requestError(platform, fullUrl, e);
            return null;
        }
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
    private static String sendRequest(String method, String path, String json, PlatformType platform) {
        String fullUrl = switch (platform) {
            case INTERNAL -> BASE_INTERNAL + path;
            case MODRINTH -> BASE_MODRINTH + path;
            case CURSEFORGE -> BASE_CURSEFORGE + path;
        };

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));

        // 添加平台对应的请求头
        switch (platform) {
            case INTERNAL ->
            {
                requestBuilder.header(HEADER_AUTHORIZATION, GlobalCache.getTrileafCertification().getMonitorAuth());
                requestBuilder.header(HEADER_SERVER_IP, GlobalCache.getServerIp());
                requestBuilder.header(HEADER_SERVER_PORT, String.valueOf(GlobalCache.getServerPort()));
            }
            case CURSEFORGE -> requestBuilder.header(HEADER_API_KEY, CURSEFORGE_API_KEY);
            case MODRINTH -> {
                // 不需要额外请求头
            }
        }

        if ("POST".equalsIgnoreCase(method)) {
            requestBuilder.POST(
                    json != null
                            ? HttpRequest.BodyPublishers.ofString(json)
                            : HttpRequest.BodyPublishers.noBody()
            );
        } else if ("PUT".equalsIgnoreCase(method)) {
            requestBuilder.PUT(
                    json != null
                            ? HttpRequest.BodyPublishers.ofString(json)
                            : HttpRequest.BodyPublishers.noBody()
            );
        }
        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            requestOk(platform, fullUrl, statusCode, body);
            return body;
        } catch (IOException | InterruptedException e) {
            requestError(platform, fullUrl, e);
            return null;
        }
    }

    /**
     * 发送 multipart/form-data 请求（包含 JSON 与文件）
     *
     * @param path        请求路径（不含 Base URL）
     * @param jsonPayload JSON 字符串（会作为 "request" 字段）
     * @param files       上传的文件列表
     * @return {@link String}
     * @author 徐亚松 2025/5/7 15:18
     */
    public static String postMultipart(String path, String jsonPayload, List<File> files) {
        String boundary = "----Boundary" + UUID.randomUUID().toString().replace("-", "");
        String fullUrl = BASE_INTERNAL + path;

        try (var byteStream = new ByteArrayOutputStream()) {
            // 1. JSON 部分
            byteStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            byteStream.write("Content-Disposition: form-data; name=\"request\"\r\n".getBytes(StandardCharsets.UTF_8));
            byteStream.write("Content-Type: application/json\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            byteStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            byteStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // 2. 文件部分
            for (File file : files) {
                byteStream.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                String fileHeader = String.format(
                        "Content-Disposition: form-data; name=\"files\"; filename=\"%s\"\r\nContent-Type: application/octet-stream\r\n\r\n",
                        file.getName()
                );
                byteStream.write(fileHeader.getBytes(StandardCharsets.UTF_8));
                byteStream.write(Files.readAllBytes(file.toPath()));
                byteStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }

            // 3. 结束边界
            byteStream.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            byte[] multipartBytes = byteStream.toByteArray();

            // 构造请求
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBytes));
            builder.header(HEADER_AUTHORIZATION, GlobalCache.getTrileafCertification().getMonitorAuth());

            HttpResponse<String> response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            requestOk(null, fullUrl, response.statusCode(), response.body());
            return response.body();
        } catch (IOException | InterruptedException e) {
            requestError(null, fullUrl, e);
            return null;
        }
    }

    /**
     * 请求失败日志
     *
     * @param platform 目标平台
     * @param fullUrl  请求地址
     * @param e        异常
     * @author 徐亚松
     * <p>2025/5/7 21:10</p>
     */
    private static void requestError(PlatformType platform, String fullUrl, Exception e) {
        LOGGER.error("请求失败，平台: {}, URL: {}", platform, fullUrl, e);
    }

    /**
     * 请求成功日志
     *
     * @param platform   目标平台
     * @param fullUrl    请求地址
     * @param statusCode 状态码
     * @param body       响应体
     * @author 徐亚松
     * <p>2025/5/7 21:10</p>
     */
    private static void requestOk(PlatformType platform, String fullUrl, int statusCode, String body) {
        LOGGER.info("请求成功，平台: {}, URL: {}, 状态码: {}, 响应: {}", platform, fullUrl, statusCode, body);
    }

    // 用于处理特殊类型参数的函数
    private static String prepareQueryParam(Object value) {
        if (value instanceof List) {
            // 如果是 List 类型，转为 JSON 字符串
            return new Gson().toJson(value);
        }
        return String.valueOf(value);  // 否则直接返回对象的字符串表示
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
