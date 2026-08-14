package com.cc.blogserver.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Sa-Token 拦截器配置
 * 拦截后台接口(用户/文章/分类/标签/项目/文件/统计/关于我维护)，放行前台公开只读接口与接口文档
 * 注意：context-path=/api，拦截路径不含 /api 前缀
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /** 精确公开的接口(方法 + 空格 + 路径) */
    private static final Set<String> PUBLIC_END_POINTS = Set.of(
            "GET /article/list",
            "GET /category/list",
            "GET /tag/list",
            "GET /project/list",
            "GET /profile"
    );

    /** 前台详情接口(单段数字ID)，与后台 /page 等路径区分 */
    private static final Pattern ARTICLE_DETAIL_PATH = Pattern.compile("^/article/\\d+$");
    private static final Pattern PROJECT_DETAIL_PATH = Pattern.compile("^/project/\\d+$");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // CORS 预检(OPTIONS)请求不携带 satoken 头，跳过登录校验
                    if (HttpMethod.OPTIONS.name().equals(SaHolder.getRequest().getMethod())) {
                        return;
                    }
                    String method = SaHolder.getRequest().getMethod();
                    String path = SaHolder.getRequest().getRequestPath();
                    if (!isPublic(method, path)) {
                        StpUtil.checkLogin();
                    }
                }))
                .addPathPatterns(
                        "/user/**",
                        "/article/**",
                        "/category/**",
                        "/tag/**",
                        "/project/**",
                        "/file/**",
                        "/stats/**",
                        "/profile/**"
                )
                .excludePathPatterns(
                        "/user/login",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**"
                );
    }

    private boolean isPublic(String method, String path) {
        if (PUBLIC_END_POINTS.contains(method + " " + path)) {
            return true;
        }
        if (HttpMethod.GET.name().equals(method)) {
            return ARTICLE_DETAIL_PATH.matcher(path).matches() || PROJECT_DETAIL_PATH.matcher(path).matches();
        }
        return false;
    }
}
