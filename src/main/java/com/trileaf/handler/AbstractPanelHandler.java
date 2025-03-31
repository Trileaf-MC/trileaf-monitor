package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.mcsm.request.MCSManagerRequest;
import com.trileaf.entity.mcsm.response.MCSManagerBaseResponse;
import com.trileaf.config.PanelResponse;
import com.trileaf.entity.mcsm.response.RemoteServiceInstancesResponse;
import okhttp3.OkHttpClient;

/**
 * 面板处理器抽象类 提供请求的基础方法
 *
 * @author 徐亚松
 * 2025/3/27 15:08
 */
public abstract class AbstractPanelHandler<T> implements PanelHandler {
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
     * @return {@link MCSManagerBaseResponse}
     * @author 徐亚松 2025/3/28 11:30
     */
    @Override
    public PanelResponse<?> getOverview() {
        return null;
    }

    /**
     * 获取实例列表
     *
     * @param request 请求参数
     * @return {@link RemoteServiceInstancesResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    @Override
    public PanelResponse<?> getRemoteServiceInstances(MCSManagerRequest param) {
        return null;
    }
}
