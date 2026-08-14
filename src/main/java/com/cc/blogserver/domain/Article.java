package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseDomain {

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** Markdown正文 */
    private String content;

    /** 封面图URL */
    private String cover;

    /** 分类ID */
    private Long categoryId;

    /** 浏览量 */
    private Integer viewCount;

    /** 状态:0草稿 1发布 2下线 */
    private Integer status;

    /** 发布时间 */
    private LocalDateTime publishTime;
}
