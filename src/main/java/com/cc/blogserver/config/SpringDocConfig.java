package com.cc.blogserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 文档配置
 * 注册 sa-token 安全方案，Swagger UI 右上角出现 Authorize 按钮
 * 点击后输入 token 值，后续请求自动携带 satoken 请求头
 */
@Configuration
public class SpringDocConfig {

    private static final String SECURITY_SCHEME_NAME = "satoken";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人博客后端 API")
                        .description("个人技术博客与项目展示平台后端接口文档")
                        .version("v1.0"))
                // 注册安全方案：sa-token 通过 header 传递，header 名为 satoken
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("satoken")
                                .description("登录后获取的 tokenValue，填入即可")))
                // 全局生效，所有接口默认携带该 token
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
