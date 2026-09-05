package dev.gad.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    public OpenAPI growAdminOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Grow Admin 后台管理系统 API")
                .description("Grow Admin 后台管理系统接口文档，接口响应统一使用 JSON 格式。")
                .version("v1"));
    }
}
