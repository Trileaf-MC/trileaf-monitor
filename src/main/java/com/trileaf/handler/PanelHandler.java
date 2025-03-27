package com.trileaf.handler;

import java.util.List;

/**
 * 面板统一接口
 *
 * @author 徐亚松
 * 2025/3/27 15:07
 */
public interface PanelHandler {
    List<Object> getUserList();

    List<Object> getInstancesList();

    /**
     * 返回该处理器对应的面板标识，例如 "mcsm"、"pterodactyl"
     */
    String getPanelType();
}
