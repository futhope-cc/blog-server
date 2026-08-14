package com.cc.blogserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 本地文件存储配置(对应 application.yaml 的 file.*)
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /** 上传根目录(相对项目根目录) */
    private String uploadDir = "./upload";

    /** 图片最大大小(字节)，默认5MB */
    private long maxImageSize = 5 * 1024 * 1024;

    /** 附件最大大小(字节)，默认20MB */
    private long maxFileSize = 20 * 1024 * 1024;
}
