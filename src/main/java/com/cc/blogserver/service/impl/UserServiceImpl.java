package com.cc.blogserver.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.blogserver.common.PageResult;
import com.cc.blogserver.converter.UserConverter;
import com.cc.blogserver.domain.User;
import com.cc.blogserver.dto.requestDTO.LoginRequestDTO;
import com.cc.blogserver.dto.requestDTO.PasswordRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserAddRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserPageRequestDTO;
import com.cc.blogserver.dto.requestDTO.UserUpdateRequestDTO;
import com.cc.blogserver.dto.responseDTO.LoginResponseDTO;
import com.cc.blogserver.dto.responseDTO.UserResponseDTO;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import com.cc.blogserver.mapper.UserMapper;
import com.cc.blogserver.service.UserService;
import com.cc.blogserver.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int USER_STATUS_DISABLED = 0;
    private static final int USER_STATUS_NORMAL = 1;
    private static final long MIN_NORMAL_USER_COUNT = 1L;
    /** 逻辑删除标记手动维护：0正常 1删除 */
    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getIsDelete, NOT_DELETED));
        // 用户不存在与密码错误统一返回，防止用户名枚举
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (Objects.equals(user.getStatus(), USER_STATUS_DISABLED)) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!PasswordUtils.verify(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }
        StpUtil.login(user.getId());
        log.info("用户登录成功, userId={}", user.getId());
        UserResponseDTO userInfo = userConverter.toResponse(user);
        return new LoginResponseDTO(SaManager.getConfig().getTokenName(), StpUtil.getTokenValue(), userInfo);
    }

    @Override
    public void logout() {
        Long userId = StpUtil.getLoginIdAsLong();
        StpUtil.logout();
        log.info("用户登出成功, userId={}", userId);
    }

    @Override
    public UserResponseDTO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getNotDeletedUserById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        return userConverter.toResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(PasswordRequestDTO request) {
        // 修改密码场景必须提供原密码，DTO 层未强制校验，此处兜底校验
        if (Objects.isNull(request.getOldPassword()) || request.getOldPassword().isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "原密码不能为空");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getNotDeletedUserById(userId);
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (!PasswordUtils.verify(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }
        User update = new User();
        update.setId(userId);
        update.setPassword(PasswordUtils.hash(request.getNewPassword()));
        userMapper.updateById(update);
        log.info("修改密码成功, userId={}", userId);
    }

    @Override
    public PageResult<UserResponseDTO> pageUsers(UserPageRequestDTO request) {
        Page<User> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                // 手动过滤已软删除记录
                .eq(User::getIsDelete, NOT_DELETED)
                .like(Objects.nonNull(request.getUsername()) && !request.getUsername().isBlank(),
                        User::getUsername, request.getUsername())
                .eq(Objects.nonNull(request.getStatus()), User::getStatus, request.getStatus())
                .orderByDesc(User::getCreateTime);
        IPage<User> result = userMapper.selectPage(page, wrapper);
        List<UserResponseDTO> records = result.getRecords().stream().map(userConverter::toResponse).toList();
        return new PageResult<>(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUser(UserAddRequestDTO request) {
        // 仅校验未删除用户名占用，软删除的同名账号允许复用
        boolean exists = userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getIsDelete, NOT_DELETED));
        if (exists) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        User user = userConverter.toEntity(request);
        user.setPassword(PasswordUtils.hash(request.getPassword()));
        if (Objects.isNull(user.getStatus())) {
            user.setStatus(USER_STATUS_NORMAL);
        }
        userMapper.insert(user);
        log.info("新增用户成功, userId={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateRequestDTO request) {
        User user = getNotDeletedUserById(id);
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        userConverter.updateEntity(request, user);
        userMapper.updateById(user);
        log.info("编辑用户成功, userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        Long currentId = StpUtil.getLoginIdAsLong();
        if (Objects.equals(id, currentId)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SELF);
        }
        Long normalCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, USER_STATUS_NORMAL)
                .eq(User::getIsDelete, NOT_DELETED));
        if (normalCount <= MIN_NORMAL_USER_COUNT) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_LAST_ADMIN);
        }
        // 逻辑删除：不走 deleteById 物理删除，改为更新 is_delete=1
        User update = new User();
        update.setId(id);
        update.setIsDelete(DELETED);
        userMapper.updateById(update);
        log.info("删除用户成功, operatorId={}, deletedId={}", currentId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, PasswordRequestDTO request) {
        User user = getNotDeletedUserById(id);
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        User update = new User();
        update.setId(id);
        update.setPassword(PasswordUtils.hash(request.getNewPassword()));
        userMapper.updateById(update);
        log.info("重置用户密码成功, userId={}", id);
    }

    /**
     * 根据ID查询未软删除的用户，不存在返回 null
     * 逻辑删除由代码手动维护，所有按ID加载均需带上 is_delete=0 条件，避免查出已删除记录
     */
    private User getNotDeletedUserById(Long id) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, id)
                .eq(User::getIsDelete, NOT_DELETED));
    }
}
