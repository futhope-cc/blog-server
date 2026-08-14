package com.cc.blogserver.converter;

import com.cc.blogserver.domain.FileRecord;
import com.cc.blogserver.dto.responseDTO.FileResponseDTO;
import org.mapstruct.Mapper;

/**
 * 文件 Domain 与 DTO 转换
 */
@Mapper(componentModel = "spring")
public interface FileRecordConverter {

    FileResponseDTO toResponse(FileRecord fileRecord);
}
