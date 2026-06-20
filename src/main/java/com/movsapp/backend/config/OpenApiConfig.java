package com.movsapp.backend.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class OpenApiConfig {
    @Bean OpenAPI movsOpenApi(){ return new OpenAPI().info(new Info().title("Movs App API").version("1.0.0").description("Contrato REST del backend de Movs App.")); }
}
