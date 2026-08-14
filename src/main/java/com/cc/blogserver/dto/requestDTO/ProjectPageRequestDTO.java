package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目分页请求(前台/后台共用)")
public class ProjectPageRequestDTO extends PageRequestDTO {

    @Schema(description = "关键字(名称模糊匹配)")
    private String keyword;

    @Schema(description = "是否首页精选:0否 1是(可选，前台筛选精选项目)")
    private Integer featured;
}
