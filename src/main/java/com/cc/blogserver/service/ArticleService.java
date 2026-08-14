package com.cc.blogserver.service;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.dto.requestDTO.ArticleAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticlePageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleStatusRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.ArticleResponseDTO;
import com.cc.blogserver.dto.responseDTO.ArticleSummaryResponseDTO;

/**
 * 文章服务
 */
public interface ArticleService {

    PageResult<ArticleSummaryResponseDTO> listPublishedArticles(ArticlePageRequestDTO request);

    ArticleResponseDTO getPublishedArticleById(Long id);

    PageResult<ArticleResponseDTO> pageArticles(ArticlePageRequestDTO request);

    Long addArticle(ArticleAddRequestDTO request);

    void updateArticle(Long id, ArticleUpdateRequestDTO request);

    void deleteArticle(Long id);

    void updateArticleStatus(Long id, ArticleStatusRequestDTO request);
}
