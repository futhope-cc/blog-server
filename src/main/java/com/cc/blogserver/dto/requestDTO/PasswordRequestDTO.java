package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码操作请求 DTO
 * 同时服务于「修改当前用户密码」与「管理员重置用户密码」两个场景：
 * - 修改密码：需提供 oldPassword（在 Service 层校验非空）+ newPassword
 * - 重置密码：仅需提供 newPassword
 * oldPassword 不加 @NotBlank，避免重置密码场景校验失败；修改密码时由 Service 层兜底校验
 */
@Data
@Schema(description = "密码操作请求(修改密码/重置密码)")
public class PasswordRequestDTO {

    @Schema(description = "原密码(修改密码时必填，重置密码时不需要)")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度需在6-50之间")
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
