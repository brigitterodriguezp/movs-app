package com.movsapp.backend.security;

import com.movsapp.backend.exception.NoAutorizadoException;
import com.movsapp.backend.exception.ProhibidoException;

public final class SecurityContext {
    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    private SecurityContext() {}

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser current() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser user = authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal
            ? principal : CURRENT.get();
        if (user == null) throw new NoAutorizadoException("Autenticación requerida.");
        return user;
    }

    public static void requireSelfOrAdmin(Long usuarioId) {
        AuthenticatedUser user = current();
        if (!user.isAdmin() && !user.id().equals(usuarioId)) {
            throw new ProhibidoException("No tiene permisos para acceder a este recurso.");
        }
    }

    public static void clear() {
        CURRENT.remove();
    }
}
