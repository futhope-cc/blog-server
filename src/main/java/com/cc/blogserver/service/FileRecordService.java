package com.cc.blogserver.service;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.dto.requestDTO.FilePageRequestDTO;
import com.cc.blogserver.dto.responseDTO.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 */
public interface FileRecordService {

    PageResult<FileResponseDTO> pageFiles(FilePageRequestDTO request);

    FileResponseDTO upload(MultipartFile file);

    void deleteFile(Long id);
}
