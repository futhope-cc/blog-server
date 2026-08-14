package com.cc.blogserver.controller;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.ArticleAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticlePageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleStatusRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.ArticleResponseDTO;
import com.cc.blogserver.dto.responseDTO.ArticleSummaryResponseDTO;
import com.cc.blogserver.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章模块：前台只读 + 后台管理
 */
@Tag(name = "文章模块", description = "前台文章列表/详情(公开)，后台文章分页/新增/编辑/删除/状态变更")
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    // ==================== 前台(公开) ====================

    @Operation(summary = "前台文章分页(仅已发布)")
    @GetMapping("/list")
    public Result<PageResult<ArticleSummaryResponseDTO>> list(@Valid ArticlePageRequestDTO request) {
        return Result.success(articleService.listPublishedArticles(request));
    }

    @Operation(summary = "前台文章详情(仅已发布，浏览量+1)")
    @GetMapping("/{id}")
    public Result<ArticleResponseDTO> detail(@Parameter(description = "文章ID") @PathVariable Long id) {
        return Result.success(articleService.getPublishedArticleById(id));
    }

    // ==================== 后台管理 ====================

    @Operation(summary = "管理文章分页(全部状态，records含正文供编辑回显)")
    @GetMapping("/page")
    public Result<PageResult<ArticleResponseDTO>> page(@Valid ArticlePageRequestDTO request) {
        return Result.success(articleService.pageArticles(request));
    }

    @Operation(summary = "新增文章")
    @PostMapping
    public Result<Long> add(@Valid @RequestBody ArticleAddRequestDTO request) {
        return Result.success(articleService.addArticle(request));
    }

    @Operation(summary = "编辑文章")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "文章ID") @PathVariable Long id,
                               @Valid @RequestBody ArticleUpdateRequestDTO request) {
        articleService.updateArticle(id, request);
        return Result.success();
    }

    @Operation(summary = "删除文章(逻辑删除)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    @Operation(summary = "变更文章状态(发布/下线)")
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@Parameter(description = "文章ID") @PathVariable Long id,
                                     @Valid @RequestBody ArticleStatusRequestDTO request) {
        articleService.updateArticleStatus(id, request);
        return Result.success();
    }
}
