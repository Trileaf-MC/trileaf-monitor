package com.trileaf.handler;

import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.factory.PanelEM;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 翼龙面板处理器
 *
 * @author 徐亚松
 * 2025/3/27 15:17
 */
@Component
public class PterodactylPanelHandler extends AbstractPanelHandler {

    private final TrileafMonitorConfig.PanelConfig panelConfig = super.getPanelConfig(this.getPanelType());

    public PterodactylPanelHandler(TrileafMonitorConfig config, OkHttpClient okHttpClient) {
        super(config, okHttpClient);
    }

    @Override
    public String getPanelType() {
        return PanelEM.PTERODACTYL.getValue();
    }
}
