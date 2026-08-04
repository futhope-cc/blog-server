package com.cc.blogserver.config;

import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MyBatis-Plus 配置：分页插件 + 自动填充
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件，单页上限 500 防恶意大页请求
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        pageInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }

    /**
     * 自动填充 createTime/updateTime/createBy/updateBy/isDelete
     * createBy/updateBy 取当前登录用户ID，未登录场景(如数据初始化)置 null
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                Long currentUserId = getCurrentUserId();
                this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
                this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
                this.strictInsertFill(metaObject, "isDelete", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
            }

            /**
             * 安全获取当前登录用户ID，未登录或非 Web 请求上下文时返回 null
             * 启动期间(如 DataInitializer 初始化默认管理员)运行在 main 线程，无 Web 请求上下文，
             * 此时 Sa-Token 的 SaTokenContext 尚未初始化，直接调用 StpUtil.isLogin() 会抛 SaTokenContextException，
             * 因此先校验请求上下文，并对 SaTokenContextException 做兜底捕获，确保非请求场景返回 null 而非中断启动
             */
            private Long getCurrentUserId() {
                if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
                    return null;
                }
                try {
                    if (StpUtil.isLogin()) {
                        return StpUtil.getLoginIdAsLong();
                    }
                } catch (SaTokenContextException e) {
                    // 请求上下文异常兜底：视为无当前登录用户
                    return null;
                }
                return null;
            }
        };
    }
}
