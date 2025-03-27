package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MCSM面板处理器
 *
 * @author 徐亚松
 * 2025/3/27 15:17
 */
@Service
public class McsmPanelHandler extends AbstractPanelHandler {
    public McsmPanelHandler(TrileafMonitorConfig config, OkHttpClient okHttpClient) {
        super(config, okHttpClient);
    }

    @Override
    public List<Object> getUserList() {
        return List.of();
    }

    @Override
    public List<Object> getInstancesList() {
        return List.of();
    }
}
