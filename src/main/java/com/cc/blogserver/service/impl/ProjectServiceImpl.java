package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.converter.ProjectConverter;
import com.cc.blogserver.domain.Project;
import com.cc.blogserver.dto.requestDTO.ProjectPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ProjectRequestDTO;
import com.cc.blogserver.dto.responseDTO.ProjectResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.ProjectMapper;
import com.cc.blogserver.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 项目服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final ProjectMapper projectMapper;
    private final ProjectConverter projectConverter;

    @Override
    public PageResult<ProjectResponseDTO> pageProjects(ProjectPageRequestDTO request) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getIsDelete, NOT_DELETED)
                .like(Objects.nonNull(request.getKeyword()) && !request.getKeyword().isBlank(),
                        Project::getName, request.getKeyword())
                .eq(Objects.nonNull(request.getFeatured()), Project::getFeatured, request.getFeatured())
                .orderByDesc(Project::getCreateTime);
        Page<Project> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<Project> result = projectMapper.selectPage(page, wrapper);
        var records = result.getRecords().stream().map(projectConverter::toResponse).toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = getNotDeletedProjectById(id);
        if (Objects.isNull(project)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_EXIST);
        }
        return projectConverter.toResponse(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProject(ProjectRequestDTO request) {
        checkNameExists(request.getName(), null);
        Project project = projectConverter.toEntity(request);
        projectMapper.insert(project);
        log.info("新增项目成功, projectId={}, name={}", project.getId(), project.getName());
        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(Long id, ProjectRequestDTO request) {
        Project project = getNotDeletedProjectById(id);
        if (Objects.isNull(project)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_EXIST);
        }
        checkNameExists(request.getName(), id);
        projectConverter.updateEntity(request, project);
        projectMapper.updateById(project);
        log.info("编辑项目成功, projectId={}, name={}", id, project.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        Project project = getNotDeletedProjectById(id);
        if (Objects.isNull(project)) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_EXIST);
        }
        Project update = new Project();
        update.setId(id);
        update.setIsDelete(DELETED);
        projectMapper.updateById(update);
        log.info("删除项目成功, projectId={}, name={}", id, project.getName());
    }

    private Project getNotDeletedProjectById(Long id) {
        return projectMapper.selectOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getId, id)
                .eq(Project::getIsDelete, NOT_DELETED));
    }

    private void checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getName, name)
                .eq(Project::getIsDelete, NOT_DELETED);
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(Project::getId, excludeId);
        }
        if (projectMapper.exists(wrapper)) {
            throw new BusinessException(ErrorCode.PROJECT_NAME_EXISTS);
        }
    }
}
