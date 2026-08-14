package com.cc.blogserver.converter;

import com.cc.blogserver.domain.Project;
import com.cc.blogserver.dto.requestDTO.ProjectRequestDTO;
import com.cc.blogserver.dto.responseDTO.ProjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 项目 Domain 与 DTO 转换
 */
@Mapper(componentModel = "spring")
public interface ProjectConverter {

    ProjectResponseDTO toResponse(Project project);

    Project toEntity(ProjectRequestDTO request);

    void updateEntity(ProjectRequestDTO request, @MappingTarget Project project);
}
