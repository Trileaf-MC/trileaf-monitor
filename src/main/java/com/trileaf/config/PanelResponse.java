package com.trileaf.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 面板响应基类
 *
 * @author 徐亚松
 * 2025-03-28 22:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanelResponse<T> {
    /**
     * 面板名称，例如 "MCSManager"、"Pterodactyl"
     */
    private String panelType;

    /**
     * 具体的数据
     */
    private T panelData;
}
