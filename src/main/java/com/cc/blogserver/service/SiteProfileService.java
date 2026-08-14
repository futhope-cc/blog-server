package com.cc.blogserver.service;

import com.cc.blogserver.dto.requestDTO.SiteProfileRequestDTO;
import com.cc.blogserver.dto.responseDTO.SiteProfileResponseDTO;

/**
 * 个人简介服务
 */
public interface SiteProfileService {

    SiteProfileResponseDTO getProfile();

    void updateProfile(SiteProfileRequestDTO request);
}
