package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.panel.PanelBaseResponse;
import com.trileaf.entity.panel.mcsm.request.MCSManagerRequest;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

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

    /**
     * 构建基础Url
     *
     * @param panelType 面板类型
     * @return {@link String}
     * @author 徐亚松 2025/3/28 09:49
     */
    protected String getBaseUrl(String panelType) {
        TrileafMonitorConfig.PanelConfig panelConfig = this.getPanelConfig(panelType);
        return panelConfig.getBaseUrl();
    }

    /**
     * 获取当前面板的配置信息
     *
     * @param panelType 面板类型
     * @return {@link TrileafMonitorConfig.PanelConfig}
     * @author 徐亚松 2025/3/28 10:34
     */
    protected TrileafMonitorConfig.PanelConfig getPanelConfig(String panelType) {
        return config.getPanel().get(panelType);
    }

    /**
     * 获取概览信息
     *
     * @return {@link PanelBaseResponse}
     * @author 徐亚松 2025/3/28 11:30
     */
    @Override
    public PanelBaseResponse<?> getOverview() {
        return null;
    }

    /**
     * 获取实例列表
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    @Override
    public PanelBaseResponse<?> getRemoteServiceInstances(MCSManagerRequest param) {
        return null;
    }

    /**
     * 获取实例详情
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse<?>}
     * @author 徐亚松 2025/3/31 14:40
     */
    @Override
    public PanelBaseResponse<?> getInstance(MCSManagerRequest param) {
        return null;
    }

    /**
     * 获取文库列表
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse<?>}
     * @author 徐亚松 2025/3/31 14:55
     */
    @Override
    public PanelBaseResponse<?> getFileList(MCSManagerRequest param) {
        return null;
    }

    /**
     * 获取文件内容
     *
     * @param param 请求参数
     * @return {@link PanelBaseResponse<?>}
     * @author 徐亚松 2025/3/31 15:27
     */
    @Override
    public PanelBaseResponse<?> getFileContent(MCSManagerRequest param,RequestBody body) {
        return null;
    }
}
