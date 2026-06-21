package com.movsapp.backend.config;

import com.movsapp.backend.security.ApiSecurityInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.*;
import java.util.Arrays;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
    private final ApiSecurityInterceptor securityInterceptor;
    @Value("${app.cors.allowed-origins}") private String allowedOrigins;
    public ApplicationConfig(ApiSecurityInterceptor securityInterceptor) { this.securityInterceptor = securityInterceptor; }
    @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(12); }
    @Override public void addCorsMappings(CorsRegistry registry){
        String[] origins=Arrays.stream(allowedOrigins.split(",")).map(String::trim).toArray(String[]::new);
        registry.addMapping("/api/**").allowedOrigins(origins).allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
            .allowedHeaders("Content-Type","Accept","Authorization").allowCredentials(false).maxAge(3600);
    }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor);
    }
}
