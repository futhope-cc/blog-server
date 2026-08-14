package com.cc.blogserver.converter;

import com.cc.blogserver.domain.Category;
import com.cc.blogserver.dto.requestDTO.CategoryRequestDTO;
import com.cc.blogserver.dto.responseDTO.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 分类 Domain 与 DTO 转换
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {

    CategoryResponseDTO toResponse(Category category);

    Category toEntity(CategoryRequestDTO request);

    void updateEntity(CategoryRequestDTO request, @MappingTarget Category category);
}
