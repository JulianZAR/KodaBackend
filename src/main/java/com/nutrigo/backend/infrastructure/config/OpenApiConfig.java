package com.nutrigo.backend.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI nutrigoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("NutriGO API")
                .version("v1")
                .description("API del sistema académico de seguimiento nutricional NutriGO."));
    }
}
