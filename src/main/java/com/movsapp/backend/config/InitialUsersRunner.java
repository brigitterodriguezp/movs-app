package com.movsapp.backend.config;

import com.movsapp.backend.service.InitialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialUsersRunner implements ApplicationRunner {
    private final InitialUserService service;
    @Value("${app.admin.name}") private String adminName;
    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.password}") private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (adminName.isBlank() || adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("Las variables APP_ADMIN_NAME, APP_ADMIN_EMAIL y APP_ADMIN_PASSWORD son obligatorias.");
        }
        service.initialize(adminName, adminEmail, adminPassword);
    }
}
