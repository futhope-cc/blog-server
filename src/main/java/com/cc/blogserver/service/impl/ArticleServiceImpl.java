package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.constant.ArticleStatus;
import com.cc.blogserver.constant.RedisKey;
import com.cc.blogserver.converter.ArticleConverter;
import com.cc.blogserver.domain.Article;
import com.cc.blogserver.domain.ArticleTag;
import com.cc.blogserver.domain.Category;
import com.cc.blogserver.domain.Tag;
import com.cc.blogserver.dto.requestDTO.ArticleAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticlePageRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleStatusRequestDTO;
import com.cc.blogserver.dto.requestDTO.ArticleUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.ArticleResponseDTO;
import com.cc.blogserver.dto.responseDTO.ArticleSummaryResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.ArticleMapper;
import com.cc.blogserver.mapper.ArticleTagMapper;
import com.cc.blogserver.mapper.CategoryMapper;
import com.cc.blogserver.mapper.TagMapper;
import com.cc.blogserver.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文章服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleConverter articleConverter;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<ArticleSummaryResponseDTO> listPublishedArticles(ArticlePageRequestDTO request) {
        List<Long> tagArticleIds = getArticleIdsByTag(request.getTagId());
        if (Objects.nonNull(request.getTagId()) && tagArticleIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, request.getCurrent(), request.getSize());
        }
        LambdaQueryWrapper<Article> wrapper = baseArticleWrapper(request)
                .eq(Article::getStatus, ArticleStatus.PUBLISHED)
                .in(Objects.nonNull(request.getTagId()), Article::getId, tagArticleIds)
                .orderByDesc(Article::getPublishTime);
        Page<Article> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<Article> result = articleMapper.selectPage(page, wrapper);
        return new PageResult<>(fillSummaries(result.getRecords()), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ArticleResponseDTO getPublishedArticleById(Long id) {
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, id)
                .eq(Article::getIsDelete, NOT_DELETED)
                .eq(Article::getStatus, ArticleStatus.PUBLISHED));
        if (Objects.isNull(article)) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        int viewCount = increaseViewCount(article);
        ArticleResponseDTO dto = fillResponse(List.of(article)).get(0);
        dto.setViewCount(viewCount);
        return dto;
    }

    @Override
    public PageResult<ArticleResponseDTO> pageArticles(ArticlePageRequestDTO request) {
        List<Long> tagArticleIds = getArticleIdsByTag(request.getTagId());
        if (Objects.nonNull(request.getTagId()) && tagArticleIds.isEmpty()) {
            return new PageResult<>(List.of(), 0L, request.getCurrent(), request.getSize());
        }
        LambdaQueryWrapper<Article> wrapper = baseArticleWrapper(request)
                .in(Objects.nonNull(request.getTagId()), Article::getId, tagArticleIds)
                .orderByDesc(Article::getUpdateTime);
        Page<Article> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<Article> result = articleMapper.selectPage(page, wrapper);
        return new PageResult<>(fillResponse(result.getRecords()), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addArticle(ArticleAddRequestDTO request) {
        checkTitleExists(request.getTitle(), null);
        checkCategoryExists(request.getCategoryId());
        checkTagsExist(request.getTagIds());
        Article article = articleConverter.toEntity(request);
        Integer status = Objects.isNull(article.getStatus()) ? ArticleStatus.DRAFT : article.getStatus();
        article.setStatus(status);
        if (Objects.equals(status, ArticleStatus.PUBLISHED)) {
            article.setPublishTime(LocalDateTime.now());
        }
        article.setViewCount(0);
        articleMapper.insert(article);
        saveArticleTags(article.getId(), request.getTagIds());
        log.info("新增文章成功, articleId={}, title={}, status={}", article.getId(), article.getTitle(), status);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Long id, ArticleUpdateRequestDTO request) {
        Article article = getNotDeletedArticleById(id);
        if (Objects.isNull(article)) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        checkTitleExists(request.getTitle(), id);
        checkCategoryExists(request.getCategoryId());
        checkTagsExist(request.getTagIds());
        articleConverter.updateEntity(request, article);
        articleMapper.updateById(article);
        // 重建标签关系：先逻辑删除旧关系，再写入新关系
        articleTagMapper.update(null, new LambdaUpdateWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, id)
                .set(ArticleTag::getIsDelete, DELETED));
        saveArticleTags(id, request.getTagIds());
        log.info("编辑文章成功, articleId={}, title={}", id, article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        Article article = getNotDeletedArticleById(id);
        if (Objects.isNull(article)) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        Article update = new Article();
        update.setId(id);
        update.setIsDelete(DELETED);
        articleMapper.updateById(update);
        articleTagMapper.update(null, new LambdaUpdateWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, id)
                .set(ArticleTag::getIsDelete, DELETED));
        log.info("删除文章成功, articleId={}, title={}", id, article.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleStatus(Long id, ArticleStatusRequestDTO request) {
        Article article = getNotDeletedArticleById(id);
        if (Objects.isNull(article)) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_EXIST);
        }
        Article update = new Article();
        update.setId(id);
        update.setStatus(request.getStatus());
        if (Objects.equals(request.getStatus(), ArticleStatus.PUBLISHED) && Objects.isNull(article.getPublishTime())) {
            update.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(update);
        log.info("变更文章状态成功, articleId={}, status={}", id, request.getStatus());
    }

    /**
     * 浏览量+1：Redis 计数(异步任务回写)，Redis 不可用时降级为不计数，保证前台接口可用
     */
    private int increaseViewCount(Article article) {
        try {
            Long delta = stringRedisTemplate.opsForValue().increment(RedisKey.ARTICLE_VIEW_COUNT.format(article.getId()));
            return article.getViewCount() + delta.intValue();
        } catch (Exception e) {
            log.warn("文章浏览量Redis计数失败, articleId={}, message={}", article.getId(), e.getMessage());
            return article.getViewCount();
        }
    }

    private LambdaQueryWrapper<Article> baseArticleWrapper(ArticlePageRequestDTO request) {
        return new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, NOT_DELETED)
                .like(Objects.nonNull(request.getKeyword()) && !request.getKeyword().isBlank(),
                        Article::getTitle, request.getKeyword())
                .eq(Objects.nonNull(request.getCategoryId()), Article::getCategoryId, request.getCategoryId())
                .eq(Objects.nonNull(request.getStatus()), Article::getStatus, request.getStatus());
    }

    private List<Long> getArticleIdsByTag(Long tagId) {
        if (Objects.isNull(tagId)) {
            return List.of();
        }
        return articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                        .select(ArticleTag::getArticleId)
                        .eq(ArticleTag::getTagId, tagId)
                        .eq(ArticleTag::getIsDelete, NOT_DELETED))
                .stream()
                .map(ArticleTag::getArticleId)
                .toList();
    }

    private List<ArticleSummaryResponseDTO> fillSummaries(List<Article> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return List.of();
        }
        Map<Long, String> categoryNames = getCategoryNames(articles);
        Map<Long, TagBundle> tagBundles = getTagBundles(articles.stream().map(Article::getId).toList());
        return articles.stream().map(article -> {
            ArticleSummaryResponseDTO dto = articleConverter.toSummary(article);
            dto.setCategoryName(categoryNames.get(article.getCategoryId()));
            dto.setTagNames(tagBundles.getOrDefault(article.getId(), TagBundle.empty()).names());
            return dto;
        }).toList();
    }

    private List<ArticleResponseDTO> fillResponse(List<Article> articles) {
        if (CollectionUtils.isEmpty(articles)) {
            return List.of();
        }
        Map<Long, String> categoryNames = getCategoryNames(articles);
        Map<Long, TagBundle> tagBundles = getTagBundles(articles.stream().map(Article::getId).toList());
        return articles.stream().map(article -> {
            ArticleResponseDTO dto = articleConverter.toResponse(article);
            dto.setCategoryName(categoryNames.get(article.getCategoryId()));
            TagBundle bundle = tagBundles.getOrDefault(article.getId(), TagBundle.empty());
            dto.setTagIds(bundle.ids());
            dto.setTagNames(bundle.names());
            return dto;
        }).toList();
    }

    private Map<Long, String> getCategoryNames(List<Article> articles) {
        List<Long> categoryIds = articles.stream()
                .map(Article::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .select(Category::getId, Category::getName)
                        .in(Category::getId, categoryIds)
                        .eq(Category::getIsDelete, NOT_DELETED))
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private Map<Long, TagBundle> getTagBundles(List<Long> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)) {
            return Map.of();
        }
        List<ArticleTag> relations = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                .select(ArticleTag::getArticleId, ArticleTag::getTagId)
                .in(ArticleTag::getArticleId, articleIds)
                .eq(ArticleTag::getIsDelete, NOT_DELETED));
        if (CollectionUtils.isEmpty(relations)) {
            return Map.of();
        }
        List<Long> tagIds = relations.stream().map(ArticleTag::getTagId).distinct().toList();
        Map<Long, String> tagNames = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                        .select(Tag::getId, Tag::getName)
                        .in(Tag::getId, tagIds)
                        .eq(Tag::getIsDelete, NOT_DELETED))
                .stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));
        return relations.stream()
                .collect(Collectors.groupingBy(ArticleTag::getArticleId,
                        Collectors.mapping(ArticleTag::getTagId, Collectors.toList())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    List<String> ids = entry.getValue().stream().map(String::valueOf).toList();
                    List<String> names = entry.getValue().stream()
                            .map(tagNames::get)
                            .filter(Objects::nonNull)
                            .toList();
                    return new TagBundle(ids, names);
                }));
    }

    private void checkTitleExists(String title, Long excludeId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getTitle, title)
                .eq(Article::getIsDelete, NOT_DELETED);
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(Article::getId, excludeId);
        }
        if (articleMapper.exists(wrapper)) {
            throw new BusinessException(ErrorCode.ARTICLE_TITLE_EXISTS);
        }
    }

    private void checkCategoryExists(Long categoryId) {
        boolean exists = categoryMapper.exists(new LambdaQueryWrapper<Category>()
                .eq(Category::getId, categoryId)
                .eq(Category::getIsDelete, NOT_DELETED));
        if (!exists) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }
    }

    private void checkTagsExist(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        for (Long tagId : tagIds) {
            boolean exists = tagMapper.exists(new LambdaQueryWrapper<Tag>()
                    .eq(Tag::getId, tagId)
                    .eq(Tag::getIsDelete, NOT_DELETED));
            if (!exists) {
                throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
            }
        }
    }

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        // 去重，避免请求内重复标签触发唯一键冲突
        List<Long> distinctTagIds = tagIds.stream().distinct().toList();
        for (Long tagId : distinctTagIds) {
            ArticleTag existing = articleTagMapper.selectOne(new LambdaQueryWrapper<ArticleTag>()
                    .eq(ArticleTag::getArticleId, articleId)
                    .eq(ArticleTag::getTagId, tagId));
            if (Objects.isNull(existing)) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                articleTagMapper.insert(articleTag);
            } else if (Objects.equals(existing.getIsDelete(), DELETED)) {
                // 复用已逻辑删除的关系记录，避免 (article_id, tag_id) 唯一键冲突
                articleTagMapper.update(null, new LambdaUpdateWrapper<ArticleTag>()
                        .eq(ArticleTag::getId, existing.getId())
                        .set(ArticleTag::getIsDelete, NOT_DELETED));
            }
        }
    }

    private Article getNotDeletedArticleById(Long id) {
        return articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, id)
                .eq(Article::getIsDelete, NOT_DELETED));
    }

    /**
     * 文章的标签关联数据：标签ID(字符串) + 标签名称
     */
    private record TagBundle(List<String> ids, List<String> names) {

        static TagBundle empty() {
            return new TagBundle(List.of(), List.of());
        }
    }
}
