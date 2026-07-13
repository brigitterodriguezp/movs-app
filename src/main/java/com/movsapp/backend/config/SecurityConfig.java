package com.movsapp.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movsapp.backend.exception.ApiError;
import com.movsapp.backend.security.BearerTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.time.OffsetDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> writeError(response, request.getRequestURI(), 401,
            "Unauthorized", "Autenticación requerida o token inválido.");
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> writeError(response, request.getRequestURI(), 403,
            "Forbidden", "No tiene permisos para acceder a este recurso.");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, BearerTokenFilter bearerTokenFilter,
                                            AuthenticationEntryPoint entryPoint,
                                            AccessDeniedHandler deniedHandler) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(errors -> errors.authenticationEntryPoint(entryPoint).accessDeniedHandler(deniedHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login", "/api/registro").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/planes/**", "/api/peliculas/**").permitAll()
                .requestMatchers("/api/usuarios/me").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/planes/**", "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/planes/**", "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/planes/**", "/api/peliculas/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/suscripciones").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/suscripciones/usuario/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/suscripciones/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/suscripciones/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/suscripciones/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    FilterRegistrationBean<BearerTokenFilter> bearerTokenFilterRegistration(BearerTokenFilter filter) {
        FilterRegistrationBean<BearerTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private void writeError(HttpServletResponse response, String path, int status, String error, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
            new ApiError(OffsetDateTime.now(), status, error, message, path, Map.of()));
    }
}
