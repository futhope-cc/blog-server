package com.cc.blogserver.converter;

import com.cc.blogserver.domain.User;
import com.cc.blogserver.dto.requestDTO.UserAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 用户 Domain 与 DTO 转换
 * mapstruct 编译期生成 UserConverterImpl，id(Long)→id(String) 自动转换防前端精度丢失
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserResponseDTO toResponse(User user);

    User toEntity(UserAddRequestDTO request);

    void updateEntity(UserUpdateRequestDTO request, @MappingTarget User user);
}
