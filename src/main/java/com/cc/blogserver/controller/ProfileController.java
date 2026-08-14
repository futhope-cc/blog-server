package com.cc.blogserver.controller;

import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.SiteProfileRequestDTO;
import com.cc.blogserver.dto.responseDTO.SiteProfileResponseDTO;
import com.cc.blogserver.service.SiteProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关于我模块：前台读取公开，后台维护
 */
@Tag(name = "关于我模块", description = "前台获取个人简介(公开)，后台维护")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SiteProfileService siteProfileService;

    @Operation(summary = "获取个人简介(公开)")
    @GetMapping
    public Result<SiteProfileResponseDTO> get() {
        return Result.success(siteProfileService.getProfile());
    }

    @Operation(summary = "更新个人简介(后台)")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SiteProfileRequestDTO request) {
        siteProfileService.updateProfile(request);
        return Result.success();
    }
}
