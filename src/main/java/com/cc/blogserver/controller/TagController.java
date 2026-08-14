package com.cc.blogserver.controller;

import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.TagRequestDTO;
import com.cc.blogserver.dto.responseDTO.TagResponseDTO;
import com.cc.blogserver.service.TagService;
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

import java.util.List;

/**
 * 标签模块：列表公开(前后台复用)，写操作需鉴权
 */
@Tag(name = "标签模块", description = "标签列表(公开)，新增/编辑/删除(后台)")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "标签列表(含关联文章数，前后台复用)")
    @GetMapping("/list")
    public Result<List<TagResponseDTO>> list() {
        return Result.success(tagService.listTags());
    }

    @Operation(summary = "新增标签")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody TagRequestDTO request) {
        return Result.success(tagService.addTag(request));
    }

    @Operation(summary = "编辑标签")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "标签ID") @PathVariable Long id,
                               @Valid @RequestBody TagRequestDTO request) {
        tagService.updateTag(id, request);
        return Result.success();
    }

    @Operation(summary = "删除标签(自动解绑关联文章)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "标签ID") @PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
