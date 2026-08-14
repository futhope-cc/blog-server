package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.blogserver.converter.SiteProfileConverter;
import com.cc.blogserver.domain.SiteProfile;
import com.cc.blogserver.dto.requestDTO.SiteProfileRequestDTO;
import com.cc.blogserver.dto.responseDTO.SiteProfileResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.SiteProfileMapper;
import com.cc.blogserver.service.SiteProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 个人简介服务实现(单行数据)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteProfileServiceImpl implements SiteProfileService {

    private static final int NOT_DELETED = 0;

    private final SiteProfileMapper siteProfileMapper;
    private final SiteProfileConverter siteProfileConverter;

    @Override
    public SiteProfileResponseDTO getProfile() {
        SiteProfile profile = getFirstProfile();
        if (Objects.isNull(profile)) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_EXIST);
        }
        return siteProfileConverter.toResponse(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(SiteProfileRequestDTO request) {
        SiteProfile profile = getFirstProfile();
        if (Objects.isNull(profile)) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_EXIST);
        }
        siteProfileConverter.updateEntity(request, profile);
        siteProfileMapper.updateById(profile);
        log.info("更新个人简介成功, profileId={}", profile.getId());
    }

    private SiteProfile getFirstProfile() {
        return siteProfileMapper.selectOne(new LambdaQueryWrapper<SiteProfile>()
                .eq(SiteProfile::getIsDelete, NOT_DELETED)
                .orderByAsc(SiteProfile::getId)
                .last("LIMIT 1"));
    }
}
