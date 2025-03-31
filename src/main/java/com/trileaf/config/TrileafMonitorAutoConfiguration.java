package com.trileaf.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 徐亚松
 * 2025-03-31 21:34
 **/
@AutoConfiguration
@ComponentScan(basePackages = "com.trileaf") // 让 Spring 发现你的 Bean
@EnableConfigurationProperties(TrileafMonitorConfig.class)
public class TrileafMonitorAutoConfiguration {
    // ✅ 手动注册 Bean，避免重复
    @Bean(name = "trileafMonitorConfig")
    public TrileafMonitorConfig trileafMonitorConfig() {
        return new TrileafMonitorConfig();
    }
}
