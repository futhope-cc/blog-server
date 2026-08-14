package com.cc.blogserver.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project")
public class Project extends BaseDomain {

    /** 项目名称 */
    private String name;

    /** 项目介绍 */
    private String description;

    /** 技术栈(逗号分隔) */
    private String technology;

    /** Github地址 */
    private String githubUrl;

    /** 项目截图URL */
    private String image;

    /** 部署方式 */
    private String deployment;

    /** 是否首页精选:0否 1是 */
    private Integer featured;
}
