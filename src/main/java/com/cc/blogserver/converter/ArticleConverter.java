package com.cc.blogserver.converter;

import com.cc.blogserver.domain.Article;
import com.cc.blogserver.dto.requestDTO.ArticleAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.ArticleResponseDTO;
import com.cc.blogserver.dto.responseDTO.ArticleSummaryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 文章 Domain 与 DTO 转换
 * categoryName/tagIds/tagNames 由 Service 层补充
 */
@Mapper(componentModel = "spring")
public interface ArticleConverter {

    ArticleResponseDTO toResponse(Article article);

    ArticleSummaryResponseDTO toSummary(Article article);

    Article toEntity(ArticleAddRequestDTO request);

    void updateEntity(ArticleUpdateRequestDTO request, @MappingTarget Article article);
}
