package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "标签响应")
public class TagResponseDTO {

    @Schema(description = "标签ID")
    private String id;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "关联文章数")
    private Long articleCount;
}
