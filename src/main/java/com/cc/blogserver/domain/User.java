package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseDomain {

    /** 用户名(登录账号，唯一) */
    private String username;

    /** 密码(BCrypt 加密存储) */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 邮箱 */
    private String email;

    /** 状态:0禁用 1正常 */
    private Integer status;
}
