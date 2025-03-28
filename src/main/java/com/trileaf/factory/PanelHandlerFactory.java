package com.trileaf.factory;

import com.trileaf.handler.PanelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 面板工厂类
 *
 * @author 徐亚松
 * 2025/3/27 15:29
 */
@Component
public class PanelHandlerFactory {
    /**
     * 面板处理器
     */
    private static final Map<String, PanelHandler> panelRegistry = new ConcurrentHashMap<>();

    /**
     * 构造方法自动注入所有实现了 PanelHandler 接口的 Bean，
     * 并根据各自的 getPanelType() 方法返回的值存入静态注册表中。
     *
     * @param handlerMap 面板map
     * @author 徐亚松 2025/3/28 09:07
     */
    @Autowired
    public PanelHandlerFactory(Map<String, PanelHandler> handlerMap) {
        panelRegistry.clear();
        handlerMap.values().forEach(handler -> panelRegistry.put(handler.getPanelType(), handler));
    }


    /**
     * 根据 panelType 返回对应的 PanelHandler 实例
     *
     * @param panelType 面板标识，如 "mcsm" 或 "pterodactyl"
     * @return {@link PanelHandler}  对应的处理器实例
     * @author 徐亚松 2025/3/28 09:07
     */
    public static PanelHandler getPanelHandler(String panelType) {
        Assert.hasText(panelType, "获取处理类失败，参数为空");
        PanelHandler handler = panelRegistry.get(panelType);
        if (!StringUtils.hasText(panelType)) {
            // 如有需要，可添加默认值处理逻辑
            throw new IllegalArgumentException("未指定有效的 panelType");
        }
        Assert.notNull(handler, "获取处理类失败，未找到 panelType：" + panelType);
        return handler;
    }
}
