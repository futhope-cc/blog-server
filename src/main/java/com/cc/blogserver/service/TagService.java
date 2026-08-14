package com.cc.blogserver.service;

import com.cc.blogserver.dto.requestDTO.TagRequestDTO;
import com.cc.blogserver.dto.responseDTO.TagResponseDTO;

import java.util.List;

/**
 * 标签服务
 */
public interface TagService {

    List<TagResponseDTO> listTags();

    Long addTag(TagRequestDTO request);

    void updateTag(Long id, TagRequestDTO request);

    void deleteTag(Long id);
}
