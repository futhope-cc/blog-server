package com.cc.blogserver.controller;

import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.responseDTO.DashboardStatsResponseDTO;
import com.cc.blogserver.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计模块
 */
@Tag(name = "数据统计模块", description = "仪表盘统计")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "仪表盘统计")
    @GetMapping("/dashboard")
    public Result<DashboardStatsResponseDTO> dashboard() {
        return Result.success(statsService.getDashboardStats());
    }
}
