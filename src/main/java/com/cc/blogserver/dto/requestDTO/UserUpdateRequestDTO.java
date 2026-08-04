package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Schema(description = "编辑用户请求(不允许修改用户名)")
public class UserUpdateRequestDTO {

    @Schema(description = "昵称")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态:0禁用 1正常")
    private Integer status;
}
