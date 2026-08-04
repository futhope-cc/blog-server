package com.cc.blogserver.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密工具，封装 hutool BCrypt
 * 登录、改密、新增、重置共用，避免散落
 */
public final class PasswordUtils {

    private static final int COST = 10;

    private PasswordUtils() {
    }

    /**
     * 加密明文密码，返回 BCrypt 哈希
     */
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(COST));
    }

    /**
     * 校验明文与哈希是否匹配
     */
    public static boolean verify(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
