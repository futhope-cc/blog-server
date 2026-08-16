-- ============================================================
-- 个人博客后端 数据库初始化脚本
-- 使用前请先创建数据库: CREATE DATABASE blog DEFAULT CHARSET utf8mb4;
-- 然后执行本脚本: source init.sql  (或在客户端中执行)
-- ============================================================

-- 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `username`    VARCHAR(50)  NOT NULL                   COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL                   COMMENT '密码(BCrypt)',
  `nickname`    VARCHAR(50)  NULL                       COMMENT '昵称',
  `avatar`      VARCHAR(255) NULL                       COMMENT '头像URL',
  `email`       VARCHAR(100) NULL                       COMMENT '邮箱',
  `status`      TINYINT      NOT NULL DEFAULT 1         COMMENT '状态:0禁用 1正常',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`),
  -- 逻辑删除场景下 username 唯一性需联合 is_delete，避免软删记录阻塞同名新建
  UNIQUE KEY `uk_username_is_delete` (`username`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 默认管理员不自动初始化。初始化方式：运行测试方法 mvn test -Dtest=BlogServerApplicationTests#initDefaultAdmin (用户名 admin, 密码 123456)

-- ============================================================
-- 文章分类表
-- ============================================================
CREATE TABLE IF NOT EXISTS `category` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `name`        VARCHAR(50)  NOT NULL                   COMMENT '分类名称',
  `sort`        INT          NOT NULL DEFAULT 0         COMMENT '排序值(升序)',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_is_delete` (`name`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- ============================================================
-- 文章标签表
-- ============================================================
CREATE TABLE IF NOT EXISTS `tag` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `name`        VARCHAR(50)  NOT NULL                   COMMENT '标签名称',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_is_delete` (`name`, `is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

-- ============================================================
-- 文章表
-- ============================================================
CREATE TABLE IF NOT EXISTS `article` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `title`        VARCHAR(200) NOT NULL                   COMMENT '标题',
  `summary`      VARCHAR(500) NULL                       COMMENT '摘要',
  `content`      LONGTEXT     NULL                       COMMENT 'Markdown正文',
  `cover`        VARCHAR(255) NULL                       COMMENT '封面图URL',
  `category_id`  BIGINT       NULL                       COMMENT '分类ID',
  `view_count`   INT          NOT NULL DEFAULT 0         COMMENT '浏览量',
  `status`       TINYINT      NOT NULL DEFAULT 0         COMMENT '状态:0草稿 1发布 2下线',
  `publish_time` DATETIME     NULL                       COMMENT '发布时间',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`    BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`    BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`    TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`       VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ============================================================
-- 文章-标签关系表
-- ============================================================
CREATE TABLE IF NOT EXISTS `article_tag` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `article_id`  BIGINT      NOT NULL                   COMMENT '文章ID',
  `tag_id`      BIGINT      NOT NULL                   COMMENT '标签ID',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT      NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT      NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT     NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                      COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关系表';

-- ============================================================
-- 项目表
-- ============================================================
CREATE TABLE IF NOT EXISTS `project` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `name`        VARCHAR(200) NOT NULL                   COMMENT '项目名称',
  `description` VARCHAR(1000) NULL                      COMMENT '项目介绍',
  `technology`  VARCHAR(255) NULL                       COMMENT '技术栈(逗号分隔)',
  `github_url`  VARCHAR(255) NULL                       COMMENT 'Github地址',
  `image`       VARCHAR(255) NULL                       COMMENT '项目截图URL',
  `deployment`  VARCHAR(255) NULL                       COMMENT '部署方式',
  `featured`    TINYINT      NOT NULL DEFAULT 0         COMMENT '是否首页精选:0否 1是',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- ============================================================
-- 文件表(本地磁盘存储)
-- ============================================================
CREATE TABLE IF NOT EXISTS `file` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `name`        VARCHAR(255) NOT NULL                   COMMENT '原始文件名',
  `url`         VARCHAR(255) NOT NULL                   COMMENT '访问URL(相对路径)',
  `type`        VARCHAR(20)  NOT NULL                   COMMENT '类型:image=图片 file=附件',
  `size`        BIGINT       NOT NULL DEFAULT 0         COMMENT '文件大小(字节)',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ============================================================
-- 个人简介表(前台"关于我"数据，单行)
-- ============================================================
CREATE TABLE IF NOT EXISTS `site_profile` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
  `nickname`    VARCHAR(50)  NULL                       COMMENT '昵称',
  `avatar`      VARCHAR(255) NULL                       COMMENT '头像URL',
  `bio`         VARCHAR(1000) NULL                      COMMENT '个人简介',
  `tech_stack`  VARCHAR(255) NULL                       COMMENT '技术栈(逗号分隔)',
  `social_links` VARCHAR(500) NULL                      COMMENT '社交链接(JSON)',
  `email`       VARCHAR(100) NULL                       COMMENT '邮箱',
  `github`      VARCHAR(255) NULL                       COMMENT 'Github主页',
  `directions`  VARCHAR(1000) NULL                      COMMENT '技术方向(JSON数组)',
  `work_experience` VARCHAR(2000) NULL                  COMMENT '工作经历(JSON数组)',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`   BIGINT       NULL                       COMMENT '创建者ID',
  `update_by`   BIGINT       NULL                       COMMENT '修改者ID',
  `is_delete`   TINYINT      NOT NULL DEFAULT 0         COMMENT '是否删除:0正常 1删除',
  `remark`      VARCHAR(255) NULL                       COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人简介表';

-- 默认个人简介(占位，可在后台或直接改库更新)
INSERT INTO `site_profile` (`nickname`, `bio`) VALUES ('站长', '个人简介待完善');

-- ============================================================
-- 存量库升级：新增 directions / work_experience 两列（全新库无需执行，已含在 CREATE TABLE 中）
-- ============================================================
ALTER TABLE `site_profile` ADD COLUMN `directions` VARCHAR(1000) NULL COMMENT '技术方向(JSON数组)' AFTER `github`;
ALTER TABLE `site_profile` ADD COLUMN `work_experience` VARCHAR(2000) NULL COMMENT '工作经历(JSON数组)' AFTER `directions`;
