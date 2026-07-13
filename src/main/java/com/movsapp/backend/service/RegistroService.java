package com.movsapp.backend.service;

import com.movsapp.backend.dto.RegistroRequest;
import com.movsapp.backend.dto.RegistroResponse;
import com.movsapp.backend.dto.SuscripcionResponse;
import com.movsapp.backend.dto.UsuarioResponse;
import com.movsapp.backend.entity.EstadoSuscripcion;
import com.movsapp.backend.entity.Plan;
import com.movsapp.backend.entity.Rol;
import com.movsapp.backend.entity.Suscripcion;
import com.movsapp.backend.entity.Usuario;
import com.movsapp.backend.exception.ConflictoException;
import com.movsapp.backend.exception.RecursoNoEncontradoException;
import com.movsapp.backend.repository.PlanRepository;
import com.movsapp.backend.repository.RolRepository;
import com.movsapp.backend.repository.SuscripcionRepository;
import com.movsapp.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegistroService {
    private static final Logger log = LoggerFactory.getLogger(RegistroService.class);
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PlanRepository planes;
    private final SuscripcionRepository suscripciones;
    private final PasswordEncoder encoder;

    @Transactional
    public RegistroResponse registrar(RegistroRequest request) {
        String correo = request.correo().trim().toLowerCase();
        log.info("Iniciando registro transaccional para correo={}", correo);
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("El correo ya se encuentra registrado.");
        }
        Plan plan = planes.findById(request.planId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Plan no encontrado."));
        Rol rol = roles.findByNombreIgnoreCase("USER")
            .orElseThrow(() -> new IllegalStateException("El rol USER no está configurado."));
        Usuario usuario = usuarios.save(Usuario.builder()
            .nombre(request.nombre().trim())
            .correo(correo)
            .passwordHash(encoder.encode(request.password()))
            .rol(rol)
            .build());
        LocalDate inicio = LocalDate.now();
        Suscripcion suscripcion = suscripciones.save(Suscripcion.builder()
            .usuario(usuario)
            .plan(plan)
            .fechaInicio(inicio)
            .fechaExpiracion(inicio.plusDays(plan.getDuracionDias()))
            .estado(EstadoSuscripcion.ACTIVA)
            .build());
        log.info("Registro transaccional completado para usuarioId={}", usuario.getId());
        return new RegistroResponse(
            new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol().getNombre()),
            new SuscripcionResponse(suscripcion.getId(), usuario.getId(), plan.getId(), plan.getNombre(),
                suscripcion.getFechaInicio(), suscripcion.getFechaExpiracion(), suscripcion.getEstado()));
    }
}
