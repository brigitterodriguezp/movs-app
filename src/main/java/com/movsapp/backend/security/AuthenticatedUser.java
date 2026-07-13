package com.movsapp.backend.security;

public record AuthenticatedUser(Long id, String correo, String rol, Long sesionId) {
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(rol);
    }
}
