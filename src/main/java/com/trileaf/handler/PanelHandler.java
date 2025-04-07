package com.trileaf.handler;

import com.trileaf.entity.panel.PanelBaseResponse;
import com.trileaf.entity.panel.mcsm.request.MCSManagerRequest;
import okhttp3.RequestBody;

/**
 * 面板统一接口
 *
 * @author 徐亚松
 * 2025/3/27 15:07
 */
public interface PanelHandler<T> {
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
     * @param vendor 厂商
     * @return {@link PanelBaseResponse} 响应
     * @author 徐亚松 2025/3/28 12:40
     */
    PanelBaseResponse<T> getOverview(String vendor);

    /**
     * 获取实例列表
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @return {@link PanelBaseResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    PanelBaseResponse<T> getRemoteServiceInstances(String vendor, MCSManagerRequest param);

    /**
     * 获取实例详情
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @return {@link PanelBaseResponse}
     * @author 徐亚松
     * <p>2025-03-28 22:20</p>
     */
    PanelBaseResponse<T> getInstance(String vendor, MCSManagerRequest param);

    /**
     * 获取文库列表
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @return {@link PanelBaseResponse<T>}
     * @author 徐亚松 2025/3/31 14:55
     */
    PanelBaseResponse<T> getFileList(String vendor, MCSManagerRequest param);

    /**
     * 获取文件内容
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @param body   body参数
     * @return {@link PanelBaseResponse<T>}
     * @author 徐亚松
     * <p>2025-04-01 22:51</p>
     */
    PanelBaseResponse<T> getFileContent(String vendor, MCSManagerRequest param, RequestBody body);

    /**
     * 解压文件
     *
     * @param vendor 厂商
     * @param param  请求参数
     * @param body   body参数
     * @return {@link PanelBaseResponse<T>}
     * @author 徐亚松
     * <p>2025-04-03 16:06</p>
     */
    PanelBaseResponse<T> decompress(String vendor, MCSManagerRequest param, RequestBody body);

}
