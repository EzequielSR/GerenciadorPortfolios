package com.example.GerenciadorPortfolios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI portfolioManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Portfolio Manager API")
                        .description("API for managing projects portfolio")
                        .version("1.0"));
    }
}