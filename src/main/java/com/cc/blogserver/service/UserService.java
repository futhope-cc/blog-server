package com.cc.blogserver.service;

import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.dto.requestDTO.LoginRequestDTO;
import com.cc.blogserver.dto.requestDTO.PasswordRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.LoginResponseDTO;
import com.cc.blogserver.dto.responseDTO.UserResponseDTO;

/**
 * 用户服务
 */
public interface UserService {

    LoginResponseDTO login(LoginRequestDTO request);

    void logout();

    UserResponseDTO getCurrentUserInfo();

    void changePassword(PasswordRequestDTO request);

    PageResult<UserResponseDTO> pageUsers(UserPageRequestDTO request);

    Long addUser(UserAddRequestDTO request);

    void updateUser(Long id, UserUpdateRequestDTO request);

    void deleteUser(Long id);

    void resetPassword(Long id, PasswordRequestDTO request);
}
