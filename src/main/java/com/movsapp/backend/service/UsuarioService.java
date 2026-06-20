package com.movsapp.backend.service;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.entity.*;
import com.movsapp.backend.exception.*;
import com.movsapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final SuscripcionRepository suscripciones;
    private final SesionRepository sesiones;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() { return usuarios.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) { return response(entidad(id)); }
    @Transactional(readOnly = true)
    public List<UsuarioResponse> porRol(String rol) { return usuarios.findByRolNombreIgnoreCase(rol).stream().map(this::response).toList(); }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (request.password() == null || request.password().isBlank()) throw new SolicitudInvalidaException("La contraseña es obligatoria.");
        String correo = request.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCase(correo)) throw new ConflictoException("El correo ya se encuentra registrado.");
        Usuario usuario = Usuario.builder().nombre(request.nombre().trim()).correo(correo)
            .passwordHash(encoder.encode(request.password())).rol(rol(request.rol())).build();
        return response(usuarios.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = entidad(id);
        String correo = request.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCaseAndIdNot(correo, id)) throw new ConflictoException("El correo ya se encuentra registrado.");
        usuario.setNombre(request.nombre().trim()); usuario.setCorreo(correo); usuario.setRol(rol(request.rol()));
        if (request.password() != null && !request.password().isBlank()) usuario.setPasswordHash(encoder.encode(request.password()));
        return response(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = entidad(id);
        sesiones.findByUsuarioId(id).ifPresent(sesiones::delete);
        suscripciones.findByUsuarioId(id).ifPresent(suscripciones::delete);
        usuarios.delete(usuario);
    }
    public Usuario entidad(Long id) { return usuarios.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado.")); }
    private Rol rol(String nombre) { return roles.findByNombreIgnoreCase(nombre.trim()).orElseThrow(() -> new SolicitudInvalidaException("El rol indicado no existe.")); }
    public UsuarioResponse response(Usuario u) { return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol().getNombre()); }
}
