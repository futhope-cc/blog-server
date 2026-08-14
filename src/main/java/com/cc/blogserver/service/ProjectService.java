package com.cc.blogserver.service;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.dto.requestDTO.ProjectPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ProjectRequestDTO;
import com.cc.blogserver.dto.responseDTO.ProjectResponseDTO;

/**
 * 项目服务
 */
public interface ProjectService {

    PageResult<ProjectResponseDTO> pageProjects(ProjectPageRequestDTO request);

    Long addProject(ProjectRequestDTO request);

    void updateProject(Long id, ProjectRequestDTO request);

    void deleteProject(Long id);

    ProjectResponseDTO getProjectById(Long id);
}
