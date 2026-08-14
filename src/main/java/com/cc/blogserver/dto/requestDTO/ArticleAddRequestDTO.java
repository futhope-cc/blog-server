package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新增文章请求")
public class ArticleAddRequestDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200")
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "摘要不能为空")
    @Size(max = 500, message = "摘要长度不能超过500")
    @Schema(description = "摘要", requiredMode = Schema.RequiredMode.REQUIRED)
    private String summary;

    @NotBlank(message = "正文不能为空")
    @Schema(description = "Markdown正文", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "状态:0草稿 1发布 2下线")
    private Integer status;
}
