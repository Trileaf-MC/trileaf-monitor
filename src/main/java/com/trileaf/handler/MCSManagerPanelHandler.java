package com.trileaf.handler;

import com.alibaba.fastjson2.JSON;
import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.mcsm.MCSManagerBaseResponse;
import com.trileaf.entity.mcsm.OverviewResponse;
import com.trileaf.factory.PanelEM;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MCSM面板处理器
 *
 * @author 徐亚松
 * 2025/3/27 15:17
 */
@Service
public class MCSManagerPanelHandler extends AbstractPanelHandler {

    private final TrileafMonitorConfig.PanelConfig panelConfig = super.getPanelConfig(this.getPanelType());

    public MCSManagerPanelHandler(TrileafMonitorConfig config, OkHttpClient okHttpClient) {
        super(config, okHttpClient);
    }

    @Override
    public String getPanelType() {
        return PanelEM.MCSMANAGER.getValue();
    }

    @Override
    public MCSManagerBaseResponse getOverview() {
        try {
            return this.executeRequest(panelConfig.getApiPaths().getOverview(), OverviewResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 发送请求并获取响应
     *
     * @param apiPath      API路径
     * @param responseType 响应类型
     * @param <T>          泛型类型
     * @return 解析后的响应对象
     * @author 徐亚松 2025/3/28 11:30
     */
    private <T> T executeRequest(String apiPath, Class<T> responseType) throws Exception {
        String url = this.buildApiUri(apiPath);

        // 创建请求
        Request request = new Request.Builder().url(url).build();
        System.out.println(url);
        try (Response response = okHttpClient.newCall(request).execute()) {
            Assert.isTrue(!response.isSuccessful(), MessageFormat.format("Unexpected HTTP response code: {0}, message: {1}", response.code(), response.message()));

            // 解析响应体
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody, responseType);
        }
    }

    /**
     * 构建基础Url
     *
     * @param apiPath 接口路径
     * @return {@link String}
     * @author 徐亚松 2025/3/28 12:39
     */
    private String buildApiUri(String apiPath) throws URISyntaxException, MalformedURLException {
        String baseUrl = this.panelConfig.getBaseUrl();
        String prefix = this.panelConfig.getApiPaths().getPrefix();

        // 处理路径拼接（自动处理多余的斜杠）
        String fullPath = Stream.of(prefix, apiPath).map(s -> s.replaceAll("^/+|/+$", "")).filter(s -> !s.isEmpty()).collect(Collectors.joining("/"));

        return new URIBuilder(baseUrl).setPath("/" + fullPath).addParameter("apikey", this.panelConfig.getApiKey()).build().toString();
    }
}
