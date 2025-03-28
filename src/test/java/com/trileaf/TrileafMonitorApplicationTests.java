package com.trileaf;

import com.alibaba.fastjson2.JSON;
import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.mcsm.MCSManagerBaseResponse;
import com.trileaf.factory.PanelEM;
import com.trileaf.factory.PanelHandlerFactory;
import com.trileaf.handler.PanelHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class TrileafMonitorApplicationTests {
@Autowired
    TrileafMonitorConfig config;
    @Test
    void contextLoads() {
        PanelHandler panelHandler = PanelHandlerFactory.getPanelHandler(PanelEM.MCSMANAGER.getValue());
        MCSManagerBaseResponse overview = panelHandler.getOverview();
        log.info(JSON.toJSONString(overview));

        PanelHandler pterodactylPanelHandler = PanelHandlerFactory.getPanelHandler(PanelEM.PTERODACTYL.getValue());
    }

}
