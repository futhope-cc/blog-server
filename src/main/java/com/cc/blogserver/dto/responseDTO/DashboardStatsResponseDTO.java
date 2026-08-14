package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "仪表盘统计响应")
public class DashboardStatsResponseDTO {

    @Schema(description = "总览统计")
    private Overview overview;

    @Schema(description = "近7周发布趋势")
    private List<TrendItem> trend;

    @Schema(description = "分类文章分布")
    private List<CategoryDistItem> categoryDist;

    @Schema(description = "热门文章TOP5")
    private List<HotArticleItem> hotArticles;

    @Data
    @Schema(description = "总览统计")
    public static class Overview {
        @Schema(description = "文章总数")
        private Long articleCount;
        @Schema(description = "项目总数")
        private Long projectCount;
        @Schema(description = "总浏览量")
        private Long viewCount;
        @Schema(description = "分类总数")
        private Long categoryCount;
        @Schema(description = "标签总数")
        private Long tagCount;
    }

    @Data
    @Schema(description = "近7周发布趋势项")
    public static class TrendItem {
        @Schema(description = "周标签(如2026-W31)")
        private String week;
        @Schema(description = "该周发布文章数")
        private Long count;
    }

    @Data
    @Schema(description = "分类分布项")
    public static class CategoryDistItem {
        @Schema(description = "分类名称")
        private String name;
        @Schema(description = "已发布文章数")
        private Long count;
    }

    @Data
    @Schema(description = "热门文章项")
    public static class HotArticleItem {
        @Schema(description = "文章ID")
        private String id;
        @Schema(description = "文章标题")
        private String title;
        @Schema(description = "浏览量")
        private Integer viewCount;
    }
}
