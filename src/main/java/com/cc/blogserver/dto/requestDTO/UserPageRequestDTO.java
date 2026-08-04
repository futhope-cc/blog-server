package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 * 继承 PageRequestDTO 复用 current/size 分页参数，本类仅保留用户业务筛选字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询请求")
public class UserPageRequestDTO extends PageRequestDTO {

    @Schema(description = "用户名(模糊匹配)")
    private String username;

    @Schema(description = "状态:0禁用 1正常")
    private Integer status;
}
