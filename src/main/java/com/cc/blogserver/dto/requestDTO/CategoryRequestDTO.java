package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分类新增/编辑请求")
public class CategoryRequestDTO {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值最小为0")
    @Max(value = 9999, message = "排序值最大为9999")
    @Schema(description = "排序值(升序)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;
}
