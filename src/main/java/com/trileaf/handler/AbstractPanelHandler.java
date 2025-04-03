package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.config.TrileafMonitorConfig.PanelConfig;
import com.trileaf.entity.panel.PanelBaseResponse;
import com.trileaf.entity.panel.mcsm.request.MCSManagerRequest;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 面板处理器抽象类
 *
 * @author 徐亚松
 * 2025/3/27 15:08
 */
public abstract class AbstractPanelHandler implements PanelHandler {
    protected final TrileafMonitorConfig config;
    protected final OkHttpClient okHttpClient;
    private PanelConfig panelConfig;  // 缓存厂商配置信息

    public AbstractPanelHandler(TrileafMonitorConfig config, OkHttpClient okHttpClient) {
        this.config = config;
        this.okHttpClient = okHttpClient;
    }


    /**
     * 获取当前面板的厂商配置信息
     *
     * @param vendor 厂商名称
     * @return {@link PanelConfig}
     * @author 徐亚松
     * <p>2025-04-01 22:46</p>
     */
    protected PanelConfig getPanelConfig(String vendor) {
        if (panelConfig == null || !vendor.equals(panelConfig.getVendor())) {
            panelConfig = loadVendorConfig(getPanelType(), vendor);
        }
        return panelConfig;
    }

    /**
     * 加载厂商配置
     *
     * @param panelType 面板类型
     * @param vendor    厂商名称
     * @return {@link PanelConfig}
     */
    private PanelConfig loadVendorConfig(String panelType, String vendor) {
        List<PanelConfig> panelConfigs = config.getPanel().get(panelType);
        Assert.notEmpty(panelConfigs, "未找到面板类型: " + panelType);
        return panelConfigs.stream()
                .filter(config -> vendor.equals(config.getVendor()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到厂商: " + vendor));
    }


    /**
     * 获取概览信息
     *
     * @return {@link PanelBaseResponse}
     * @author 徐亚松 2025/3/28 11:30
     */
    @Override
    public PanelBaseResponse<?> getOverview(String vendor) {
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
    public PanelBaseResponse<?> getRemoteServiceInstances(String vendor,MCSManagerRequest param) {
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
    public PanelBaseResponse<?> getInstance(String vendor,MCSManagerRequest param) {
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
    public PanelBaseResponse<?> getFileList(String vendor,MCSManagerRequest param) {
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
    public PanelBaseResponse<?> getFileContent(String vendor,MCSManagerRequest param, RequestBody body) {
        return null;
    }

    /**
     * 解压文件
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @param body   body参数
     * @return {@link PanelBaseResponse<?>}
     * @author 徐亚松
     * <p>2025-04-02 19:02</p>
     */
    @Override
    public PanelBaseResponse<?> decompress(String vendor, MCSManagerRequest param, RequestBody body) {
        return null;
    }
}
