package com.cc.blogserver.controller;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.ProjectPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ProjectRequestDTO;
import com.cc.blogserver.dto.responseDTO.ProjectResponseDTO;
import com.cc.blogserver.service.ProjectService;
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
 * 项目模块：前台只读 + 后台管理
 */
@Tag(name = "项目模块", description = "前台项目分页/详情(公开)，后台项目分页/新增/编辑/删除")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // ==================== 前台(公开) ====================

    @Operation(summary = "前台项目分页(可按精选筛选)")
    @GetMapping("/list")
    public Result<PageResult<ProjectResponseDTO>> list(@Valid ProjectPageRequestDTO request) {
        return Result.success(projectService.pageProjects(request));
    }

    @Operation(summary = "前台项目详情")
    @GetMapping("/{id}")
    public Result<ProjectResponseDTO> detail(@Parameter(description = "项目ID") @PathVariable Long id) {
        return Result.success(projectService.getProjectById(id));
    }

    // ==================== 后台管理 ====================

    @Operation(summary = "管理项目分页")
    @GetMapping("/page")
    public Result<PageResult<ProjectResponseDTO>> page(@Valid ProjectPageRequestDTO request) {
        return Result.success(projectService.pageProjects(request));
    }

    @Operation(summary = "新增项目")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody ProjectRequestDTO request) {
        return Result.success(projectService.addProject(request));
    }

    @Operation(summary = "编辑项目")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "项目ID") @PathVariable Long id,
                               @Valid @RequestBody ProjectRequestDTO request) {
        projectService.updateProject(id, request);
        return Result.success();
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "项目ID") @PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}
