package com.movsapp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MovsAppBackendApplication extends SpringBootServletInitializer {
    static {
        if (System.getProperty("debug") == null) System.setProperty("debug", "false");
    }
    private static final Logger log = LoggerFactory.getLogger(MovsAppBackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovsAppBackendApplication.class, args);
        log.info("Movs App inició correctamente");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MovsAppBackendApplication.class);
    }
}
