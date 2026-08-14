package com.cc.blogserver.controller;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.common.Result;
import com.cc.blogserver.dto.requestDTO.FilePageRequestDTO;
import com.cc.blogserver.dto.responseDTO.FileResponseDTO;
import com.cc.blogserver.service.FileRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件模块(本地磁盘存储)
 */
@Tag(name = "文件模块", description = "文件分页/上传/删除(后台，上传后返回可访问URL)")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileRecordService fileRecordService;

    @Operation(summary = "文件分页")
    @GetMapping("/page")
    public Result<PageResult<FileResponseDTO>> page(@Valid FilePageRequestDTO request) {
        return Result.success(fileRecordService.pageFiles(request));
    }

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FileResponseDTO> upload(@Parameter(description = "上传文件") @RequestParam("file") MultipartFile file) {
        return Result.success(fileRecordService.upload(file));
    }

    @Operation(summary = "删除文件(逻辑删除)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        fileRecordService.deleteFile(id);
        return Result.success();
    }
}
