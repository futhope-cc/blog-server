package com.cc.blogserver.controller;

import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.LoginRequestDTO;
import com.cc.blogserver.dto.requestDTO.PasswordRequestDTO;
import com.cc.blogserver.dto.responseDTO.LoginResponseDTO;
import com.cc.blogserver.dto.responseDTO.UserResponseDTO;
import com.cc.blogserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员鉴权：登录、登出、当前用户、修改密码
 */
@Tag(name = "管理员鉴权", description = "管理员登录、登出、当前用户、修改密码")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final UserService userService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/user/info")
    public Result<UserResponseDTO> getCurrentUserInfo() {
        return Result.success(userService.getCurrentUserInfo());
    }

    @Operation(summary = "修改当前用户密码")
    @PutMapping("/user/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordRequestDTO request) {
        userService.changePassword(request);
        return Result.success();
    }
}
