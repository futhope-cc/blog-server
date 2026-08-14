package com.cc.blogserver.service;

import com.cc.blogserver.dto.responseDTO.DashboardStatsResponseDTO;

/**
 * 数据统计服务
 */
public interface StatsService {

    DashboardStatsResponseDTO getDashboardStats();
}
