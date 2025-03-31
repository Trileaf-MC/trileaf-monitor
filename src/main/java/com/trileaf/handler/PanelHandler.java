package com.trileaf.handler;

import com.trileaf.config.PanelResponse;
import com.trileaf.entity.mcsm.request.MCSManagerRequest;
import com.trileaf.entity.mcsm.response.MCSManagerBaseResponse;
import com.trileaf.entity.mcsm.response.RemoteServiceInstancesResponse;

/**
 * 面板统一接口
 *
 * @author 徐亚松
 * 2025/3/27 15:07
 */
public interface PanelHandler {
    /**
     * 返回该处理器对应的面板标识，例如 "MCSManager"、"Pterodactyl"
     *
     * @return {@link String} 面板名称
     * @author 徐亚松 2025/3/28 12:40
     */
    String getPanelType();

    /**
     * 获取概览信息
     *
     * @return {@link MCSManagerBaseResponse} 响应
     * @author 徐亚松 2025/3/28 12:40
     */
    PanelResponse<?> getOverview();

    /**
     * 获取实例列表
     *
     * @param param 请求参数
     * @return {@link RemoteServiceInstancesResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    PanelResponse<?> getRemoteServiceInstances(MCSManagerRequest param);


}
