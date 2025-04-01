package com.trileaf.handler;

import com.alibaba.fastjson2.JSON;
import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.panel.PanelBaseResponse;
import com.trileaf.entity.panel.mcsm.request.MCSManagerRequest;
import com.trileaf.entity.panel.mcsm.response.*;
import com.trileaf.factory.PanelEM;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * 获取处理器类型
     *
     * @return {@link String}
     * @author 徐亚松
     * <p>2025-03-28 21:42</p>
     */
    @Override
    public String getPanelType() {
        return PanelEM.MCSMANAGER.getValue();
    }

    /**
     * 获取概览信息
     *
     * @return {@link MCSManagerBaseResponse}
     * @author 徐亚松
     * <p>2025-03-28 21:41</p>
     */
    @Override
    public PanelBaseResponse<OverviewResponse> getOverview(String vendor) {
        try {
            OverviewResponse data = this.executeRequest(
                    super.getPanelConfig(vendor).getApiPaths().getOverview(),
                    "GET", null,
                    null, OverviewResponse.class,vendor);
            return new PanelBaseResponse<>(this.getPanelType(), data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取实例列表
     *
     * @param param 请求参数
     * @return {@link RemoteServiceInstancesResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    @Override
    public PanelBaseResponse<RemoteServiceInstancesResponse> getRemoteServiceInstances(String vendor,MCSManagerRequest param) {
        try {
            Map<String, String> queryParams = this.convertRequestToQueryParams(param);
            RemoteServiceInstancesResponse data = this.executeRequest(
                    super.getPanelConfig(vendor).getApiPaths().getInstancesList(),
                    "GET", queryParams,
                    null, RemoteServiceInstancesResponse.class,vendor);
            return new PanelBaseResponse<>(this.getPanelType(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取实例详情
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse<InstanceDetailResponse>}
     * @author 徐亚松 2025/3/31 14:43
     */
    @Override
    public PanelBaseResponse<InstanceDetailResponse> getInstance(String vendor,MCSManagerRequest param) {
        try {
            Map<String, String> queryParams = this.convertRequestToQueryParams(param);
            InstanceDetailResponse data = this.executeRequest(
                    super.getPanelConfig(vendor).getApiPaths().getInstanceDetail(),
                    "GET", queryParams,
                    null, InstanceDetailResponse.class,vendor);
            return new PanelBaseResponse<>(this.getPanelType(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文库列表
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse<FileListResponse>}
     * @author 徐亚松 2025/3/31 14:55
     */
    @Override
    public PanelBaseResponse<FileListResponse> getFileList(String vendor,MCSManagerRequest param) {
        try {
            Map<String, String> queryParams = this.convertRequestToQueryParams(param);
            FileListResponse data = this.executeRequest(
                    panelConfig.getApiPaths().getFileList(),
                    "GET", queryParams,
                    null, FileListResponse.class,vendor);
            return new PanelBaseResponse<>(this.getPanelType(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件内容
     *
     * @param body 请求体
     * @return {@link PanelBaseResponse<FileContentResponse>}
     * @author 徐亚松 2025/3/31 15:44
     */
    @Override
    public PanelBaseResponse<FileContentResponse> getFileContent(String vendor,MCSManagerRequest param, RequestBody body) {
        try {
            Map<String, String> queryParams = this.convertRequestToQueryParams(param);
            FileContentResponse data = this.executeRequest(
                    super.getPanelConfig(vendor).getApiPaths().getFileContent(),
                    "PUT", queryParams,
                    body, FileContentResponse.class,vendor);
            return new PanelBaseResponse<>(this.getPanelType(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射将对象转为map参数
     *
     * @param param 参数
     * @return {@link Map<String,String>}
     * @author 徐亚松 2025/3/31 14:41
     */
    private Map<String, String> convertRequestToQueryParams(MCSManagerRequest param) {
        Map<String, String> queryParams = new HashMap<>();
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(param);
                // 如果字段不为null，则无论值是多少，都加入参数（包括0和空字符串）
                if (value != null) {
                    queryParams.put(field.getName(), String.valueOf(value));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return queryParams;
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
    private <T> T executeRequest(String apiPath, String httpMethod, Map<String, String> queryParams, RequestBody body, Class<T> responseType, String vendor) throws Exception {
        TrileafMonitorConfig.PanelConfig panelConfig = super.getPanelConfig(vendor);
        // 构造完整 URL，同时添加固定的apikey参数及其他query参数
        String url = this.buildApiUri(apiPath, queryParams,panelConfig);
        System.out.println(url);
        // 创建请求构造器
        Request.Builder builder = new Request.Builder().url(url);

        // 根据 httpMethod 处理请求体
        if ("POST".equalsIgnoreCase(httpMethod) || "PUT".equalsIgnoreCase(httpMethod)) {
            builder.method(httpMethod, body);
        } else {
            // GET 或其他方法不带请求体
            builder.method(httpMethod, null);
        }

        Request request = builder.build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            Assert.isTrue(response.isSuccessful(),
                    MessageFormat.format("Unexpected HTTP response code: {0}, message: {1}", response.code(), response.message()));

            String responseBody = response.body().string();
            T t = JSON.parseObject(responseBody, responseType);
            return t;
        }
    }

    /**
     * 构建基础Url
     *
     * @param apiPath     接口路径
     * @param queryParams 参数
     * @return {@link String}
     * @author 徐亚松 2025/3/28 12:39
     */
    private String buildApiUri(String apiPath, Map<String, String> queryParams, TrileafMonitorConfig.PanelConfig panelConfig) throws URISyntaxException {
        String baseUrl = panelConfig.getBaseUrl();
        String prefix = panelConfig.getApiPaths().getPrefix();

        // 处理路径拼接（自动处理多余的斜杠）
        String fullPath = Stream.of(prefix, apiPath)
                .map(s -> s.replaceAll("^/+|/+$", ""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("/"));

        URIBuilder uriBuilder = new URIBuilder(baseUrl)
                .setPath("/" + fullPath)
                // 固定添加一个apikey参数
                .addParameter("apikey", this.panelConfig.getApiKey());

        // 添加额外的 query 参数（如果有）
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder.build().toString();
    }
}
