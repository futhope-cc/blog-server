package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 个人简介实体(前台"关于我"数据，单行)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("site_profile")
public class SiteProfile extends BaseDomain {

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 个人简介 */
    private String bio;

    /** 技术栈(逗号分隔) */
    private String techStack;

    /** 社交链接(JSON) */
    private String socialLinks;

    /** 邮箱 */
    private String email;

    /** Github主页 */
    private String github;

    /** 技术方向(JSON数组: title/icon/desc) */
    private String directions;

    /** 工作经历(JSON数组: company/position/period/desc) */
    private String workExperience;
}
