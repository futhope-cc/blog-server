package com.cc.blogserver.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * 0 成功；4xx/5xx HTTP 语义码；1xxx 业务码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),

    PARAM_INVALID(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    USER_NOT_EXIST(1002, "用户不存在"),
    USER_DISABLED(1003, "用户已被禁用"),
    USERNAME_EXISTS(1004, "用户名已存在"),
    OLD_PASSWORD_ERROR(1005, "原密码错误"),
    CANNOT_DELETE_SELF(1006, "不能删除自己"),
    CANNOT_DELETE_LAST_ADMIN(1007, "不能删除最后一个管理员"),

    CATEGORY_NAME_EXISTS(1008, "分类名称已存在"),
    CATEGORY_NOT_EXIST(1009, "分类不存在"),
    CATEGORY_HAS_ARTICLES(1010, "该分类下存在文章，无法删除"),
    TAG_NAME_EXISTS(1011, "标签名称已存在"),
    TAG_NOT_EXIST(1012, "标签不存在"),
    ARTICLE_NOT_EXIST(1013, "文章不存在"),
    ARTICLE_TITLE_EXISTS(1014, "文章标题已存在"),
    PROJECT_NOT_EXIST(1015, "项目不存在"),
    PROJECT_NAME_EXISTS(1016, "项目名称已存在"),
    FILE_UPLOAD_FAILED(1017, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(1018, "不支持的文件类型"),
    FILE_NOT_EXIST(1019, "文件不存在"),
    PROFILE_NOT_EXIST(1020, "个人简介不存在"),

    SYSTEM_ERROR(500, "系统错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
