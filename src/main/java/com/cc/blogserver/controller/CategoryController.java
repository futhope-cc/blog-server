package com.cc.blogserver.controller;

import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.CategoryRequestDTO;
import com.cc.blogserver.dto.responseDTO.CategoryResponseDTO;
import com.cc.blogserver.service.CategoryService;
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
 * 分类模块：列表公开(前后台复用)，写操作需鉴权
 */
@Tag(name = "分类模块", description = "分类列表(公开)，新增/编辑/删除(后台)")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类列表(含已发布文章数，前后台复用)")
    @GetMapping("/list")
    public Result<List<CategoryResponseDTO>> list() {
        return Result.success(categoryService.listCategories());
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody CategoryRequestDTO request) {
        return Result.success(categoryService.addCategory(request));
    }

    @Operation(summary = "编辑分类")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "分类ID") @PathVariable Long id,
                               @Valid @RequestBody CategoryRequestDTO request) {
        categoryService.updateCategory(id, request);
        return Result.success();
    }

    @Operation(summary = "删除分类(分类下有文章不可删除)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
