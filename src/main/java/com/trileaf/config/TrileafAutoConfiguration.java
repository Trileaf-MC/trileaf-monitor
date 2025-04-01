package com.trileaf.config;

import com.trileaf.factory.PanelHandlerFactory;
import com.trileaf.factory.YamlPropertySourceFactory;
import com.trileaf.handler.PanelHandler;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * 三叶云控自动配置类
 *
 * @author 徐亚松
 * 2025/4/1 09:13
 */
@AutoConfiguration
@EnableConfigurationProperties(TrileafMonitorConfig.class)
@ComponentScan(basePackages = "com.trileaf.handler")
@PropertySource(
        value = "classpath:trileaf-monitor-defaults.yml",
        factory = YamlPropertySourceFactory.class
)
public class TrileafAutoConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    /**
     * 加载处理器工厂
     *
     * @author 徐亚松
     * 2025/4/1 11:00
     */
    @Bean
    public PanelHandlerFactory panelHandlerFactory(List<PanelHandler<?>> handlerList) {
        return new PanelHandlerFactory(handlerList);
    }

}
