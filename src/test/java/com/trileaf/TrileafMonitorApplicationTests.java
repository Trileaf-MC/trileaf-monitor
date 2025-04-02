package com.trileaf;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.trileaf.config.TrileafMonitorConfig;
import com.trileaf.entity.panel.PanelBaseResponse;
import com.trileaf.entity.panel.mcsm.request.MCSManagerRequest;
import com.trileaf.entity.panel.mcsm.response.*;
import com.trileaf.factory.PanelEM;
import com.trileaf.factory.PanelHandlerFactory;
import com.trileaf.handler.PanelHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class TrileafMonitorApplicationTests {
    @Autowired
    private PanelHandlerFactory factory;

    //获取MCSM处理器
    PanelHandler<MCSManagerBaseResponse> panelHandler;

    @PostConstruct
    public void init() {
        panelHandler = factory.getHandler(PanelEM.MCSMANAGER.getValue());
    }

    /**
     * 获取概览信息
     *
     * @author 徐亚松
     * 2025/3/31 15:03
     */
    @Test
    void getOverview() {
        //获取MCSM处理器
        PanelHandler<MCSManagerBaseResponse> panelHandler = factory.getHandler(PanelEM.MCSMANAGER.getValue());

        PanelBaseResponse<MCSManagerBaseResponse> HuYeOverview = panelHandler.getOverview("HuYe");
        PanelBaseResponse<MCSManagerBaseResponse> XuuuOverview = panelHandler.getOverview("Xuuu");

        OverviewResponse HuYePanelData = (OverviewResponse) HuYeOverview.getPanelData();
        log.info(JSON.toJSONString(HuYePanelData));

        OverviewResponse XuuuPanelData = (OverviewResponse) XuuuOverview.getPanelData();
        log.info(JSON.toJSONString(XuuuPanelData));
    }

    /**
     * 获取实例列表
     *
     * @author 徐亚松
     * 2025/3/31 15:03
     */
    @Test
    void getRemoteServiceInstances() {
        MCSManagerRequest param = MCSManagerRequest.builder().remote_uuid("3a8c564899ac4e47ba2dba53fe87977e").page(1).page_size(144).instance_name("Xu_test").status("3").build();
        PanelBaseResponse<MCSManagerBaseResponse> responseData = panelHandler.getRemoteServiceInstances("",param);
        RemoteServiceInstancesResponse response = (RemoteServiceInstancesResponse) responseData.getPanelData();
        log.info(JSON.toJSONString(response));
    }

    /**
     * 获取实例详情
     *
     * @author 徐亚松
     * 2025/3/31 15:02
     */
    @Test
    void getInstance() {
        MCSManagerRequest param = MCSManagerRequest.builder()
                .remote_uuid("3a8c564899ac4e47ba2dba53fe87977e")
                .uuid("cf95d0dc627e40f0bf4dddbf92a4b66e").build();
        PanelBaseResponse<MCSManagerBaseResponse> responseData = panelHandler.getInstance("",param);
        InstanceDetailResponse response = (InstanceDetailResponse) responseData.getPanelData();
        log.info(JSON.toJSONString(response));
    }

    /**
     * 获取文件列表
     *
     * @author 徐亚松
     * 2025/3/31 15:02
     */
    @Test
    void getFileList() {
        MCSManagerRequest param = MCSManagerRequest.builder()
                .remote_uuid("3a8c564899ac4e47ba2dba53fe87977e")
                .uuid("cf95d0dc627e40f0bf4dddbf92a4b66e")
                .target("/mods")
                .file_name("")
                .page(0)
                .page_size(20)
                .build();
        PanelBaseResponse<MCSManagerBaseResponse> responseData = panelHandler.getFileList("",param);
        FileListResponse response = (FileListResponse) responseData.getPanelData();
        log.info(JSON.toJSONString(response));
    }

    /**
     * 获取文件内容
     *
     * @author 徐亚松
     * 2025/3/31 16:05
     */
    @Test
    void getFileContent() {
        MCSManagerRequest param = MCSManagerRequest.builder()
                .remote_uuid("3a8c564899ac4e47ba2dba53fe87977e")
                .uuid("cf95d0dc627e40f0bf4dddbf92a4b66e")
                .build();

        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("target", "/eula.txt");
        jsonObject.put("target", "/mods/FallingTree-1.20.1-4.3.4.jar");
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonObject.toJSONString());

        PanelBaseResponse<MCSManagerBaseResponse> responseData = panelHandler.getFileContent("",param, requestBody);
        FileContentResponse response = (FileContentResponse) responseData.getPanelData();
        log.info(JSON.toJSONString(response));
    }

}
