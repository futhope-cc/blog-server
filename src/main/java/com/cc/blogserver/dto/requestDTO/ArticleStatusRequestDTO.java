package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "文章状态变更请求")
public class ArticleStatusRequestDTO {

    @NotNull(message = "状态不能为空")
    @Min(value = 1, message = "状态仅支持发布或下线")
    @Max(value = 2, message = "状态仅支持发布或下线")
    @Schema(description = "状态:1发布 2下线", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}
