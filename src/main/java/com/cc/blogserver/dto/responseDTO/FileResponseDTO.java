package com.cc.blogserver.dto.responseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "文件响应")
public class FileResponseDTO {

    @Schema(description = "文件ID")
    private String id;

    @Schema(description = "原始文件名")
    private String name;

    @Schema(description = "访问URL(相对路径)")
    private String url;

    @Schema(description = "类型:image=图片 file=附件")
    private String type;

    @Schema(description = "文件大小(字节)")
    private Long size;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
