package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "个人简介响应(前台关于我)")
public class SiteProfileResponseDTO {

    @Schema(description = "简介ID")
    private String id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "技术栈")
    private String techStack;

    @Schema(description = "社交链接(JSON)")
    private String socialLinks;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "Github主页")
    private String github;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
