package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "项目新增/编辑请求")
public class ProjectRequestDTO {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "项目介绍不能为空")
    @Size(max = 1000, message = "项目介绍长度不能超过1000")
    @Schema(description = "项目介绍", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "技术栈(逗号分隔)")
    private String technology;

    @Schema(description = "Github地址")
    private String githubUrl;

    @Schema(description = "项目截图URL")
    private String image;

    @Schema(description = "部署方式")
    private String deployment;

    @NotNull(message = "是否首页精选不能为空")
    @Min(value = 0, message = "是否首页精选仅支持0/1")
    @Max(value = 1, message = "是否首页精选仅支持0/1")
    @Schema(description = "是否首页精选:0否 1是", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer featured;
}
