package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "个人简介编辑请求")
public class SiteProfileRequestDTO {

    @Size(max = 50, message = "昵称长度不能超过50")
    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Size(max = 1000, message = "个人简介长度不能超过1000")
    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "技术栈(逗号分隔)")
    private String techStack;

    @Schema(description = "社交链接(JSON)")
    private String socialLinks;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "Github主页")
    private String github;

    @Size(max = 1000, message = "技术方向长度不能超过1000")
    @Schema(description = "技术方向(JSON数组:[{title,icon,desc}])")
    private String directions;

    @Size(max = 2000, message = "工作经历长度不能超过2000")
    @Schema(description = "工作经历(JSON数组:[{company,position,period,desc}])")
    private String workExperience;

    @Size(max = 100, message = "备案号长度不能超过100")
    @Schema(description = "备案号")
    private String copyright;
}
