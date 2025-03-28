package com.trileaf.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 面板枚举类
 *
 * @author 徐亚松
 * 2025/3/27 14:10
 */
@Getter
@AllArgsConstructor
public enum PanelEM {

    MCSMANAGER("MCSManager", "MCSManager"),
    PTERODACTYL("Pterodactyl", "Pterodactyl");

    private final String value;
    private final String desc;

    public static String getDesc(String value) {
        PanelEM[] statuses = values();
        for (PanelEM status : statuses) {
            if (status.getValue().equals(value)) {
                return status.getDesc();
            }
        }
        return null;
    }
}
