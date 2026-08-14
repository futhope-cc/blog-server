package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.blogserver.converter.CategoryConverter;
import com.cc.blogserver.domain.Article;
import com.cc.blogserver.domain.Category;
import com.cc.blogserver.dto.requestDTO.CategoryRequestDTO;
import com.cc.blogserver.dto.responseDTO.CategoryResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.ArticleMapper;
import com.cc.blogserver.mapper.CategoryMapper;
import com.cc.blogserver.service.CategoryService;
import com.cc.blogserver.constant.ArticleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final CategoryConverter categoryConverter;

    @Override
    public List<CategoryResponseDTO> listCategories() {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getIsDelete, NOT_DELETED)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId));
        if (categories.isEmpty()) {
            return List.of();
        }
        List<Long> categoryIds = categories.stream().map(Category::getId).toList();
        Map<Long, Long> countMap = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .select(Article::getCategoryId)
                        .eq(Article::getIsDelete, NOT_DELETED)
                        .eq(Article::getStatus, ArticleStatus.PUBLISHED)
                        .in(Article::getCategoryId, categoryIds))
                .stream()
                .collect(Collectors.groupingBy(Article::getCategoryId, Collectors.counting()));
        return categories.stream().map(category -> {
            CategoryResponseDTO dto = categoryConverter.toResponse(category);
            dto.setArticleCount(countMap.getOrDefault(category.getId(), 0L));
            return dto;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCategory(CategoryRequestDTO request) {
        checkNameExists(request.getName(), null);
        Category category = categoryConverter.toEntity(request);
        categoryMapper.insert(category);
        log.info("新增分类成功, categoryId={}, name={}", category.getId(), category.getName());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryRequestDTO request) {
        Category category = getNotDeletedCategoryById(id);
        if (Objects.isNull(category)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }
        checkNameExists(request.getName(), id);
        categoryConverter.updateEntity(request, category);
        categoryMapper.updateById(category);
        log.info("编辑分类成功, categoryId={}, name={}", id, category.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Category category = getNotDeletedCategoryById(id);
        if (Objects.isNull(category)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }
        boolean hasArticles = articleMapper.exists(new LambdaQueryWrapper<Article>()
                .eq(Article::getCategoryId, id)
                .eq(Article::getIsDelete, NOT_DELETED));
        if (hasArticles) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_ARTICLES);
        }
        Category update = new Category();
        update.setId(id);
        update.setIsDelete(DELETED);
        categoryMapper.updateById(update);
        log.info("删除分类成功, categoryId={}, name={}", id, category.getName());
    }

    private Category getNotDeletedCategoryById(Long id) {
        return categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getId, id)
                .eq(Category::getIsDelete, NOT_DELETED));
    }

    private void checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name)
                .eq(Category::getIsDelete, NOT_DELETED);
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(Category::getId, excludeId);
        }
        if (categoryMapper.exists(wrapper)) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
    }
}
