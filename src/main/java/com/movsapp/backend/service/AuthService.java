package com.movsapp.backend.service;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.entity.*;
import com.movsapp.backend.exception.*;
import com.movsapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class AuthService {
    public static final String SESION_DUPLICADA = "El usuario ya tiene una sesión activa. Cierre la sesión anterior antes de iniciar nuevamente.";
    private final UsuarioRepository usuarios;
    private final SesionRepository sesiones;
    private final PasswordEncoder encoder;

    @Transactional
    public SesionResponse login(LoginRequest request) {
        Usuario usuario=usuarios.findByCorreoIgnoreCase(request.correo().trim()).orElseThrow(() -> new NoAutorizadoException("Correo o contraseña incorrectos."));
        if(!encoder.matches(request.password(),usuario.getPasswordHash())) throw new NoAutorizadoException("Correo o contraseña incorrectos.");
        Sesion sesion=sesiones.findForUpdateByUsuarioId(usuario.getId()).orElse(null);
        if(sesion!=null && sesion.isActiva()) throw new ConflictoException(SESION_DUPLICADA);
        LocalDateTime ahora=LocalDateTime.now();
        if(sesion==null) sesion=Sesion.builder().usuario(usuario).activa(true).fechaInicio(ahora).build();
        else { sesion.setActiva(true); sesion.setFechaInicio(ahora); sesion.setFechaCierre(null); }
        return response(sesiones.save(sesion));
    }
    @Transactional
    public void logout(Long usuarioId) {
        Sesion sesion=sesiones.findForUpdateByUsuarioId(usuarioId).orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada."));
        if(!sesion.isActiva()) throw new ConflictoException("El usuario no tiene una sesión activa.");
        sesion.setActiva(false); sesion.setFechaCierre(LocalDateTime.now());
    }
    @Transactional(readOnly=true)
    public SesionResponse sesion(Long usuarioId) {
        return response(sesiones.findByUsuarioId(usuarioId).orElseThrow(() -> new RecursoNoEncontradoException("Sesión no encontrada.")));
    }
    private SesionResponse response(Sesion s){ return new SesionResponse(s.getId(),s.getUsuario().getId(),s.getUsuario().getCorreo(),s.getUsuario().getRol().getNombre(),s.isActiva(),s.getFechaInicio(),s.getFechaCierre()); }
}
