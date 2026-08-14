package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.blogserver.constant.ArticleStatus;
import com.cc.blogserver.domain.Article;
import com.cc.blogserver.domain.Category;
import com.cc.blogserver.domain.Project;
import com.cc.blogserver.domain.Tag;
import com.cc.blogserver.dto.responseDTO.DashboardStatsResponseDTO;
import com.cc.blogserver.mapper.ArticleMapper;
import com.cc.blogserver.mapper.CategoryMapper;
import com.cc.blogserver.mapper.ProjectMapper;
import com.cc.blogserver.mapper.TagMapper;
import com.cc.blogserver.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final int NOT_DELETED = 0;
    private static final int TREND_WEEK_COUNT = 7;
    private static final int HOT_ARTICLE_LIMIT = 5;

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ProjectMapper projectMapper;

    @Override
    public DashboardStatsResponseDTO getDashboardStats() {
        DashboardStatsResponseDTO result = new DashboardStatsResponseDTO();
        result.setOverview(buildOverview());
        result.setTrend(buildTrend());
        result.setCategoryDist(buildCategoryDist());
        result.setHotArticles(buildHotArticles());
        return result;
    }

    private DashboardStatsResponseDTO.Overview buildOverview() {
        DashboardStatsResponseDTO.Overview overview = new DashboardStatsResponseDTO.Overview();
        overview.setArticleCount(articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, NOT_DELETED)));
        overview.setProjectCount(projectMapper.selectCount(new LambdaQueryWrapper<Project>()
                .eq(Project::getIsDelete, NOT_DELETED)));
        overview.setCategoryCount(categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getIsDelete, NOT_DELETED)));
        overview.setTagCount(tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getIsDelete, NOT_DELETED)));
        long viewCount = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .select(Article::getViewCount)
                        .eq(Article::getIsDelete, NOT_DELETED))
                .stream()
                .mapToLong(article -> Objects.isNull(article.getViewCount()) ? 0L : article.getViewCount())
                .sum();
        overview.setViewCount(viewCount);
        return overview;
    }

    private List<DashboardStatsResponseDTO.TrendItem> buildTrend() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<LocalDate, Long> countByWeekStart = new LinkedHashMap<>();
        LocalDate currentWeekStart = today.with(weekFields.dayOfWeek(), 1L);
        for (int i = TREND_WEEK_COUNT - 1; i >= 0; i--) {
            countByWeekStart.put(currentWeekStart.minusWeeks(i), 0L);
        }
        List<Article> publishedArticles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getPublishTime)
                .eq(Article::getIsDelete, NOT_DELETED)
                .eq(Article::getStatus, ArticleStatus.PUBLISHED)
                .isNotNull(Article::getPublishTime));
        for (Article article : publishedArticles) {
            LocalDate weekStart = article.getPublishTime().toLocalDate().with(weekFields.dayOfWeek(), 1L);
            countByWeekStart.computeIfPresent(weekStart, (key, count) -> count + 1);
        }
        return countByWeekStart.entrySet().stream().map(entry -> {
            DashboardStatsResponseDTO.TrendItem item = new DashboardStatsResponseDTO.TrendItem();
            int week = entry.getKey().get(weekFields.weekOfWeekBasedYear());
            int year = entry.getKey().get(weekFields.weekBasedYear());
            item.setWeek(year + "-W" + String.format("%02d", week));
            item.setCount(entry.getValue());
            return item;
        }).toList();
    }

    private List<DashboardStatsResponseDTO.CategoryDistItem> buildCategoryDist() {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId, Category::getName)
                .eq(Category::getIsDelete, NOT_DELETED));
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
            DashboardStatsResponseDTO.CategoryDistItem item = new DashboardStatsResponseDTO.CategoryDistItem();
            item.setName(category.getName());
            item.setCount(countMap.getOrDefault(category.getId(), 0L));
            return item;
        }).toList();
    }

    private List<DashboardStatsResponseDTO.HotArticleItem> buildHotArticles() {
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .select(Article::getId, Article::getTitle, Article::getViewCount)
                        .eq(Article::getIsDelete, NOT_DELETED)
                        .eq(Article::getStatus, ArticleStatus.PUBLISHED)
                        .orderByDesc(Article::getViewCount)
                        .last("LIMIT " + HOT_ARTICLE_LIMIT))
                .stream()
                .map(article -> {
                    DashboardStatsResponseDTO.HotArticleItem item = new DashboardStatsResponseDTO.HotArticleItem();
                    item.setId(String.valueOf(article.getId()));
                    item.setTitle(article.getTitle());
                    item.setViewCount(article.getViewCount());
                    return item;
                })
                .toList();
    }
}
