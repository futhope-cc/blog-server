package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章分页请求(前台/后台共用)")
public class ArticlePageRequestDTO extends PageRequestDTO {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "关键字(标题模糊匹配)")
    private String keyword;

    @Schema(description = "状态:0草稿 1发布 2下线(后台分页使用，前台只查已发布)")
    private Integer status;
}
