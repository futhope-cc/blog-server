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

-- 默认管理员由 DataInitializer 在应用启动时自动初始化(用户名 admin, 密码 123456)
