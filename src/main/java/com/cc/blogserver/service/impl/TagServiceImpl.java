package com.cc.blogserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cc.blogserver.converter.TagConverter;
import com.cc.blogserver.domain.ArticleTag;
import com.cc.blogserver.domain.Tag;
import com.cc.blogserver.dto.requestDTO.TagRequestDTO;
import com.cc.blogserver.dto.responseDTO.TagResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.ArticleTagMapper;
import com.cc.blogserver.mapper.TagMapper;
import com.cc.blogserver.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagConverter tagConverter;

    @Override
    public List<TagResponseDTO> listTags() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getIsDelete, NOT_DELETED)
                .orderByAsc(Tag::getId));
        if (tags.isEmpty()) {
            return List.of();
        }
        List<Long> tagIds = tags.stream().map(Tag::getId).toList();
        Map<Long, Long> countMap = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                        .select(ArticleTag::getTagId)
                        .eq(ArticleTag::getIsDelete, NOT_DELETED)
                        .in(ArticleTag::getTagId, tagIds))
                .stream()
                .collect(Collectors.groupingBy(ArticleTag::getTagId, Collectors.counting()));
        return tags.stream().map(tag -> {
            TagResponseDTO dto = tagConverter.toResponse(tag);
            dto.setArticleCount(countMap.getOrDefault(tag.getId(), 0L));
            return dto;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTag(TagRequestDTO request) {
        checkNameExists(request.getName(), null);
        Tag tag = tagConverter.toEntity(request);
        tagMapper.insert(tag);
        log.info("新增标签成功, tagId={}, name={}", tag.getId(), tag.getName());
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(Long id, TagRequestDTO request) {
        Tag tag = getNotDeletedTagById(id);
        if (Objects.isNull(tag)) {
            throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
        }
        checkNameExists(request.getName(), id);
        tagConverter.updateEntity(request, tag);
        tagMapper.updateById(tag);
        log.info("编辑标签成功, tagId={}, name={}", id, tag.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        Tag tag = getNotDeletedTagById(id);
        if (Objects.isNull(tag)) {
            throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
        }
        // 自动解绑关联：逻辑删除该标签下所有文章标签关系
        articleTagMapper.update(null, new LambdaUpdateWrapper<ArticleTag>()
                .eq(ArticleTag::getTagId, id)
                .set(ArticleTag::getIsDelete, DELETED));
        Tag update = new Tag();
        update.setId(id);
        update.setIsDelete(DELETED);
        tagMapper.updateById(update);
        log.info("删除标签成功, tagId={}, name={}", id, tag.getName());
    }

    private Tag getNotDeletedTagById(Long id) {
        return tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getId, id)
                .eq(Tag::getIsDelete, NOT_DELETED));
    }

    private void checkNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, name)
                .eq(Tag::getIsDelete, NOT_DELETED);
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(Tag::getId, excludeId);
        }
        if (tagMapper.exists(wrapper)) {
            throw new BusinessException(ErrorCode.TAG_NAME_EXISTS);
        }
    }
}
