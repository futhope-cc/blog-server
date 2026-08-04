package com.cc.blogserver;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.blogserver.domain.User;
import com.cc.blogserver.mapper.UserMapper;
import com.cc.blogserver.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Objects;

@Slf4j
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class BlogServerApplicationTests {

    private final UserMapper userMapper;

    @Test
    void contextLoads() {
    }

    /**
     * 初始化默认管理员 admin/123456
     * 手动执行此测试方法即可，避免启动时自动初始化
     */
    @Test
    void initDefaultAdmin() {
        boolean exists = userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "admin")
                .eq(User::getIsDelete, 0));
        if (exists) {
            log.info("默认管理员已存在, 跳过初始化, username={}", "admin");
            return;
        }
        User user = new User();
        user.setUsername("admin");
        user.setPassword(PasswordUtils.hash("123456"));
        user.setNickname("管理员");
        user.setStatus(1);
        userMapper.insert(user);
        log.info("默认管理员初始化完成, username={}, id={}", "admin", user.getId());
    }
}
