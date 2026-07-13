package com.movsapp.backend.service;

import com.movsapp.backend.entity.EstadoSuscripcion;
import com.movsapp.backend.entity.Plan;
import com.movsapp.backend.entity.Rol;
import com.movsapp.backend.entity.Suscripcion;
import com.movsapp.backend.entity.Usuario;
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
public class InitialUserService {
    private static final Logger log = LoggerFactory.getLogger(InitialUserService.class);
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PlanRepository planes;
    private final SuscripcionRepository suscripciones;
    private final PasswordEncoder encoder;

    @Transactional
    public void initialize(String adminName, String adminEmail, String adminPassword) {
        Rol adminRole = requiredRole("ADMIN");
        Rol userRole = requiredRole("USER");
        if (!usuarios.existsByCorreoIgnoreCase(adminEmail)) {
            Usuario admin = usuarios.save(newUser(adminName, adminEmail, adminPassword, adminRole));
            log.info("Administrador inicial creado con usuarioId={}", admin.getId());
        } else {
            log.info("Administrador inicial ya existe; no se sobrescribe");
        }

        Usuario alejandra = usuarios.findByCorreoIgnoreCase("alejandra@gmail.com").orElseGet(() -> {
            Usuario created = usuarios.save(newUser("Alejandra", "alejandra@gmail.com", "alejandra.2005", userRole));
            log.info("Usuario académico Alejandra creado con usuarioId={}", created.getId());
            return created;
        });
        if (!suscripciones.existsByUsuarioId(alejandra.getId())) {
            Plan plan = planes.findById(1L).orElseThrow(() -> new IllegalStateException("El plan inicial no está configurado."));
            LocalDate inicio = LocalDate.now();
            suscripciones.save(Suscripcion.builder().usuario(alejandra).plan(plan).fechaInicio(inicio)
                .fechaExpiracion(inicio.plusDays(plan.getDuracionDias())).estado(EstadoSuscripcion.ACTIVA).build());
            log.info("Suscripción inicial creada para usuarioId={}", alejandra.getId());
        }
    }

    private Usuario newUser(String name, String email, String password, Rol role) {
        return Usuario.builder().nombre(name.trim()).correo(email.trim().toLowerCase())
            .passwordHash(encoder.encode(password)).rol(role).build();
    }

    private Rol requiredRole(String name) {
        return roles.findByNombreIgnoreCase(name)
            .orElseThrow(() -> new IllegalStateException("El rol " + name + " no está configurado."));
    }
}
