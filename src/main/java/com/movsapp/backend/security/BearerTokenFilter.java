package com.movsapp.backend.security;

import com.movsapp.backend.entity.Sesion;
import com.movsapp.backend.exception.NoAutorizadoException;
import com.movsapp.backend.repository.SesionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BearerTokenFilter extends OncePerRequestFilter {
    private final TokenService tokens;
    private final SesionRepository sesiones;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    @Value("${app.security.require-https:false}") private boolean requireHttps;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            chain.doFilter(request, response);
            return;
        }
        try {
            if (requireHttps && !request.isSecure()) throw new NoAutorizadoException("HTTPS requerido.");
            if (!header.startsWith("Bearer ") || header.substring(7).isBlank()) {
                throw new NoAutorizadoException("Token Bearer inválido.");
            }
            TokenService.Claims claims = tokens.verify(header.substring(7).trim());
            Sesion sesion = sesiones.findWithUsuarioById(claims.sesionId())
                .orElseThrow(() -> new NoAutorizadoException("Sesión no encontrada."));
            if (!sesion.isActiva() || !sesion.getUsuario().getId().equals(claims.usuarioId())) {
                throw new NoAutorizadoException("Sesión inválida o cerrada.");
            }
            String role = claims.rol().toUpperCase();
            AuthenticatedUser principal = new AuthenticatedUser(claims.usuarioId(), claims.correo(), role, claims.sesionId());
            var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response,
                new org.springframework.security.authentication.BadCredentialsException(ex.getMessage(), ex));
            return;
        }
        chain.doFilter(request, response);
    }
}
