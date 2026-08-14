package com.cc.blogserver.converter;

import com.cc.blogserver.domain.Tag;
import com.cc.blogserver.dto.requestDTO.TagRequestDTO;
import com.cc.blogserver.dto.responseDTO.TagResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 标签 Domain 与 DTO 转换
 */
@Mapper(componentModel = "spring")
public interface TagConverter {

    TagResponseDTO toResponse(Tag tag);

    Tag toEntity(TagRequestDTO request);

    void updateEntity(TagRequestDTO request, @MappingTarget Tag tag);
}
