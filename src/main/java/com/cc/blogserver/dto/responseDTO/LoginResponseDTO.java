package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponseDTO {

    @Schema(description = "token 名称")
    private String tokenName;

    @Schema(description = "token 值")
    private String tokenValue;

    @Schema(description = "当前登录用户信息")
    private UserResponseDTO userInfo;
}
