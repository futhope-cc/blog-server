package com.cc.blogserver.dto.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件分页请求")
public class FilePageRequestDTO extends PageRequestDTO {

    @Schema(description = "类型:image=图片 file=附件")
    private String type;

    @Schema(description = "关键字(文件名模糊匹配)")
    private String keyword;
}
