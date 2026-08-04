package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页请求基类
 * 所有分页查询请求 DTO 继承此类，统一管理 current/size 参数，避免在各业务 DTO 中重复定义
 */
@Data
@Schema(description = "分页请求基类")
public class PageRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "当前页码", defaultValue = "1")
    private Long current = 1L;

    @Min(value = 1, message = "每页条数最小为1")
    @Schema(description = "每页条数", defaultValue = "10")
    private Long size = 10L;
}
