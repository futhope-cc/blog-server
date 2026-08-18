package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.config.FileStorageProperties;
import com.cc.blogserver.constant.FileType;
import com.cc.blogserver.converter.FileRecordConverter;
import com.cc.blogserver.domain.Article;
import com.cc.blogserver.domain.FileRecord;
import com.cc.blogserver.domain.Project;
import com.cc.blogserver.domain.SiteProfile;
import com.cc.blogserver.domain.User;
import com.cc.blogserver.dto.requestDTO.FilePageRequestDTO;
import com.cc.blogserver.dto.responseDTO.FileResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.ArticleMapper;
import com.cc.blogserver.mapper.FileRecordMapper;
import com.cc.blogserver.mapper.ProjectMapper;
import com.cc.blogserver.mapper.SiteProfileMapper;
import com.cc.blogserver.mapper.UserMapper;
import com.cc.blogserver.service.FileRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 文件服务实现(本地磁盘存储)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileRecordServiceImpl implements FileRecordService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;
    private static final String URL_PREFIX = "/files/";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "ico");
    private static final Set<String> FILE_EXTENSIONS = Set.of(
            "pdf", "zip", "rar", "7z", "tar", "gz",
            "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "md", "mp3", "mp4", "wav", "m4a", "apk", "jar");

    private final FileRecordMapper fileRecordMapper;
    private final FileRecordConverter fileRecordConverter;
    private final FileStorageProperties properties;
    private final ArticleMapper articleMapper;
    private final ProjectMapper projectMapper;
    private final SiteProfileMapper siteProfileMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<FileResponseDTO> pageFiles(FilePageRequestDTO request) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<FileRecord>()
                .eq(FileRecord::getIsDelete, NOT_DELETED)
                .eq(Objects.nonNull(request.getType()) && !request.getType().isBlank(),
                        FileRecord::getType, request.getType())
                .like(Objects.nonNull(request.getKeyword()) && !request.getKeyword().isBlank(),
                        FileRecord::getName, request.getKeyword())
                .orderByDesc(FileRecord::getCreateTime);
        Page<FileRecord> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<FileRecord> result = fileRecordMapper.selectPage(page, wrapper);
        var records = result.getRecords().stream().map(fileRecordConverter::toResponse).toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileResponseDTO upload(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
        }
        String originalName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "unknown";
        String extension = getExtension(originalName);
        String type = detectType(extension);
        checkSize(file.getSize(), type);

        String datePath = LocalDate.now().format(DATE_FORMAT);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetPath = Paths.get(properties.getUploadDir(), datePath, fileName);
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件写入失败, name={}, message={}", originalName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        String url = URL_PREFIX + datePath + "/" + fileName;

        FileRecord record = new FileRecord();
        record.setName(originalName);
        record.setUrl(url);
        record.setType(type);
        record.setSize(file.getSize());
        fileRecordMapper.insert(record);
        log.info("文件上传成功, fileId={}, name={}, type={}, size={}", record.getId(), originalName, type, file.getSize());
        return fileRecordConverter.toResponse(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        FileRecord record = getNotDeletedFileById(id);
        if (Objects.isNull(record)) {
            throw new BusinessException(ErrorCode.FILE_NOT_EXIST);
        }
        // 检查文件是否被其他业务引用
        checkFileInUse(record.getUrl());
        // 删除物理文件
        deletePhysicalFile(record.getUrl());
        // 逻辑删除数据库记录
        FileRecord update = new FileRecord();
        update.setId(id);
        update.setIsDelete(DELETED);
        fileRecordMapper.updateById(update);
        log.info("删除文件成功, fileId={}, name={}, url={}", id, record.getName(), record.getUrl());
    }

    private FileRecord getNotDeletedFileById(Long id) {
        return fileRecordMapper.selectOne(new LambdaQueryWrapper<FileRecord>()
                .eq(FileRecord::getId, id)
                .eq(FileRecord::getIsDelete, NOT_DELETED));
    }

    /**
     * 检查文件是否被文章封面、项目截图、用户头像、个人简介头像引用
     */
    private void checkFileInUse(String url) {
        if (articleMapper.exists(new LambdaQueryWrapper<Article>()
                .eq(Article::getCover, url).eq(Article::getIsDelete, NOT_DELETED))) {
            throw new BusinessException(ErrorCode.FILE_IN_USE);
        }
        if (projectMapper.exists(new LambdaQueryWrapper<Project>()
                .eq(Project::getImage, url).eq(Project::getIsDelete, NOT_DELETED))) {
            throw new BusinessException(ErrorCode.FILE_IN_USE);
        }
        if (siteProfileMapper.exists(new LambdaQueryWrapper<SiteProfile>()
                .eq(SiteProfile::getAvatar, url).eq(SiteProfile::getIsDelete, NOT_DELETED))) {
            throw new BusinessException(ErrorCode.FILE_IN_USE);
        }
        if (userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getAvatar, url).eq(User::getIsDelete, NOT_DELETED))) {
            throw new BusinessException(ErrorCode.FILE_IN_USE);
        }
    }

    /**
     * 根据文件访问URL删除磁盘物理文件，文件不存在时静默忽略
     */
    private void deletePhysicalFile(String url) {
        if (!StringUtils.hasText(url) || !url.startsWith(URL_PREFIX)) {
            return;
        }
        String relativePath = url.substring(URL_PREFIX.length());
        Path filePath = Paths.get(properties.getUploadDir(), relativePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("物理文件删除失败, url={}, message={}", url, e.getMessage());
        }
    }

    private String getExtension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return Objects.isNull(extension) ? "" : extension.toLowerCase(Locale.ROOT);
    }

    private String detectType(String extension) {
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return FileType.IMAGE;
        }
        if (FILE_EXTENSIONS.contains(extension)) {
            return FileType.FILE;
        }
        throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
    }

    private void checkSize(long size, String type) {
        long limit = Objects.equals(type, FileType.IMAGE) ? properties.getMaxImageSize() : properties.getMaxFileSize();
        if (size > limit) {
            String hint = Objects.equals(type, FileType.IMAGE)
                    ? "图片大小不能超过" + formatSize(properties.getMaxImageSize())
                    : "附件大小不能超过" + formatSize(properties.getMaxFileSize());
            throw new BusinessException(ErrorCode.PARAM_INVALID, hint);
        }
    }

    private String formatSize(long bytes) {
        if (bytes >= 1024 * 1024) {
            return (bytes / (1024 * 1024)) + "MB";
        }
        return (bytes / 1024) + "KB";
    }
}
