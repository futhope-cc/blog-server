package com.cc.blogserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 本地文件存储静态资源映射
 * 访问路径 /files/** → 上传目录(URL 为 /api/files/...，context-path=/api)
 */
@Configuration
@RequiredArgsConstructor
public class FileStorageConfig implements WebMvcConfigurer {

    private final FileStorageProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + Paths.get(properties.getUploadDir()).toAbsolutePath().normalize() + "/";
        registry.addResourceHandler("/files/**").addResourceLocations(location);
    }
}
