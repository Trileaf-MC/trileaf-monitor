package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.http.HttpHeaders;

/**
 * 面板处理器抽象类 提供请求的基础方法
 *
 * @author 徐亚松
 * 2025/3/27 15:08
 */
public abstract class AbstractPanelHandler implements PanelHandler {
    protected final TrileafMonitorConfig config;
    protected final OkHttpClient okHttpClient;

    public AbstractPanelHandler(TrileafMonitorConfig config, OkHttpClient okHttpClient) {
        this.config = config;
        this.okHttpClient = okHttpClient;
    }

    protected String buildUrl(String path) {
        config.getPanel()
        return config.getBaseUrl() + config.getApiPaths().getPrefix() + path;
    }

    protected Request createRequestWithHeaders(String url) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        // 添加公共请求头
        // requestBuilder.addHeader("Authorization", "Bearer " + config.getApiKey()); // 示例：添加API密钥
        // 可以继续添加其他需要的头部信息

        return requestBuilder.build();
    }

    // 使用示例
    protected Response makeGetRequest(String path) throws IOException {
        String url = this.buildUrl(path);
        Request request = createRequestWithHeaders(url);

        return okHttpClient.newCall(request).execute();
    }
}
