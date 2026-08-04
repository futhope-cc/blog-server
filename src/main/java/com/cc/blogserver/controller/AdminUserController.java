package com.cc.blogserver.controller;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.PasswordRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.UserResponseDTO;
import com.cc.blogserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理：分页、新增、编辑、删除、重置密码
 */
@Tag(name = "用户管理", description = "用户分页、新增、编辑、删除、重置密码")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<PageResult<UserResponseDTO>> page(@Valid UserPageRequestDTO request) {
        return Result.success(userService.pageUsers(request));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody UserAddRequestDTO request) {
        return Result.success(userService.addUser(request));
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "用户ID") @PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequestDTO request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id,
                                      @Valid @RequestBody PasswordRequestDTO request) {
        userService.resetPassword(id, request);
        return Result.success();
    }
}
