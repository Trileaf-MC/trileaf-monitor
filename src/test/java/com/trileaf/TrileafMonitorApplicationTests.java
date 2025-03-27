package com.trileaf;

import com.alibaba.fastjson2.JSON;
import com.trileaf.config.TrileafMonitorConfig;
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
        System.out.println(JSON.toJSONString(config));
        System.out.println( JSON.toJSONString(config.getPanel()));
    }

}
