package com.movsapp.backend.security;

import com.movsapp.backend.entity.Sesion;
import com.movsapp.backend.exception.NoAutorizadoException;
import com.movsapp.backend.exception.ProhibidoException;
import com.movsapp.backend.exception.SolicitudInvalidaException;
import com.movsapp.backend.repository.SesionRepository;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ApiSecurityInterceptor implements HandlerInterceptor {
    private final TokenService tokens;
    private final SesionRepository sesiones;
    @Value("${app.security.require-https:false}") private boolean requireHttps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!request.getRequestURI().startsWith("/api/")) return true;
        if (requireHttps && !request.isSecure()) throw new SolicitudInvalidaException("HTTPS requerido.");
        if (isPublic(request)) return true;

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) throw new NoAutorizadoException("Autenticación requerida.");

        TokenService.Claims claims = tokens.verify(header.substring(7).trim());
        Sesion sesion = sesiones.findById(claims.sesionId())
            .orElseThrow(() -> new NoAutorizadoException("Sesión no encontrada."));
        if (!sesion.isActiva() || !sesion.getUsuario().getId().equals(claims.usuarioId())) {
            throw new NoAutorizadoException("Sesión inválida o cerrada.");
        }

        AuthenticatedUser user = new AuthenticatedUser(claims.usuarioId(), claims.correo(), claims.rol(), claims.sesionId());
        SecurityContext.set(user);
        requireRoleIfNeeded(handler, user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private boolean isPublic(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
            || "/api/auth/login".equals(uri)
            || ("/api/planes".equals(uri) && "GET".equalsIgnoreCase(request.getMethod()))
            || ("/api/usuarios".equals(uri) && "POST".equalsIgnoreCase(request.getMethod()))
            || uri.startsWith("/swagger-ui")
            || uri.startsWith("/v3/api-docs");
    }

    private void requireRoleIfNeeded(Object handler, AuthenticatedUser user) {
        if (!(handler instanceof HandlerMethod method)) return;
        RequireRole required = method.getMethodAnnotation(RequireRole.class);
        if (required == null) required = method.getBeanType().getAnnotation(RequireRole.class);
        if (required == null) return;
        boolean allowed = Arrays.stream(required.value()).anyMatch(role -> role.equalsIgnoreCase(user.rol()));
        if (!allowed) throw new ProhibidoException("No tiene permisos para acceder a este recurso.");
    }
}
