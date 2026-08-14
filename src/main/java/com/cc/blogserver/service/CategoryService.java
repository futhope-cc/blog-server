package com.cc.blogserver.service;

import com.cc.blogserver.dto.requestDTO.CategoryRequestDTO;
import com.cc.blogserver.dto.responseDTO.CategoryResponseDTO;

import java.util.List;

/**
 * 分类服务
 */
public interface CategoryService {

    List<CategoryResponseDTO> listCategories();

    Long addCategory(CategoryRequestDTO request);

    void updateCategory(Long id, CategoryRequestDTO request);

    void deleteCategory(Long id);
}
