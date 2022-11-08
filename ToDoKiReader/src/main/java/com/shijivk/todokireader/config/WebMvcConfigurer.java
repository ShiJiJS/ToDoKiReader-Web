package com.shijivk.todokireader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    @Value("${cachePath}")
    private String cachePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置本地缓存
        registry.addResourceHandler("/temp/**").addResourceLocations("file:" + cachePath + File.separator);
        super.addResourceHandlers(registry);
    }
}
