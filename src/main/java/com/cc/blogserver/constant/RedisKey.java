package com.cc.blogserver.constant;

import lombok.Getter;

/**
 * Redis Key 统一管理，禁止字符串拼接
 * 使用: RedisKey.USER_INFO.format(userId)
 */
@Getter
public enum RedisKey {

    USER_INFO("user:info:%s"),
    USER_LOGIN_CAPTCHA("user:captcha:%s");

    private final String pattern;

    RedisKey(String pattern) {
        this.pattern = pattern;
    }

    public String format(Object... args) {
        return String.format(pattern, args);
    }
}
