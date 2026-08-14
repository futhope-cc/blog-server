package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "项目响应")
public class ProjectResponseDTO {

    @Schema(description = "项目ID")
    private String id;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目介绍")
    private String description;

    @Schema(description = "技术栈")
    private String technology;

    @Schema(description = "Github地址")
    private String githubUrl;

    @Schema(description = "项目截图URL")
    private String image;

    @Schema(description = "部署方式")
    private String deployment;

    @Schema(description = "是否首页精选:0否 1是")
    private Integer featured;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
