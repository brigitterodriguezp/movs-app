package com.movsapp.backend.security;

import com.movsapp.backend.exception.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimitService {
    private final int maxAttempts;
    private final Duration window;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public LoginRateLimitService(@Value("${app.security.login-max-attempts:5}") int maxAttempts,
                                 @Value("${app.security.login-window-minutes:1}") long windowMinutes) {
        this.maxAttempts = maxAttempts;
        this.window = Duration.ofMinutes(windowMinutes);
    }

    public void check(String key) {
        Instant now = Instant.now();
        Bucket bucket = buckets.compute(key, (ignored, current) -> {
            if (current == null || now.isAfter(current.resetAt)) return new Bucket(1, now.plus(window));
            return new Bucket(current.attempts + 1, current.resetAt);
        });
        if (bucket.attempts > maxAttempts) {
            throw new SolicitudInvalidaException("Demasiados intentos de inicio de sesión. Intente nuevamente más tarde.");
        }
    }

    public void reset(String key) {
        buckets.remove(key);
    }

    private record Bucket(int attempts, Instant resetAt) {}
}
