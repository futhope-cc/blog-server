package com.cc.blogserver.converter;

import com.cc.blogserver.domain.SiteProfile;
import com.cc.blogserver.dto.requestDTO.SiteProfileRequestDTO;
import com.cc.blogserver.dto.responseDTO.SiteProfileResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 个人简介 Domain 与 DTO 转换
 */
@Mapper(componentModel = "spring")
public interface SiteProfileConverter {

    SiteProfileResponseDTO toResponse(SiteProfile siteProfile);

    void updateEntity(SiteProfileRequestDTO request, @MappingTarget SiteProfile siteProfile);
}
