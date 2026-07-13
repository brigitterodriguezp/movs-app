package com.movsapp.backend.service;

import com.movsapp.backend.dto.RegistroRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTests {
    @Mock UsuarioRepository usuarios;
    @Mock RolRepository roles;
    @Mock PlanRepository planes;
    @Mock SuscripcionRepository suscripciones;
    @Mock PasswordEncoder encoder;
    RegistroService service;
    RegistroRequest request;

    @BeforeEach
    void setUp() {
        service = new RegistroService(usuarios, roles, planes, suscripciones, encoder);
        request = new RegistroRequest("Alejandra", "alejandra@gmail.com", "alejandra.2005", 1L);
    }

    @Test
    void rechazaCorreoDuplicadoAntesDeCrearDatos() {
        when(usuarios.existsByCorreoIgnoreCase("alejandra@gmail.com")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> service.registrar(request));
        verify(usuarios, never()).save(any());
        verify(suscripciones, never()).save(any());
    }

    @Test
    void rechazaPlanInexistenteAntesDeCrearUsuario() {
        when(usuarios.existsByCorreoIgnoreCase("alejandra@gmail.com")).thenReturn(false);
        when(planes.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(request));
        verify(usuarios, never()).save(any());
    }

    @Test
    void asignaSiempreUserYCreaSuscripcionConExpiracion() {
        Plan plan = Plan.builder().id(1L).nombre("Basic").duracionDias(30).build();
        Rol user = Rol.builder().id(2L).nombre("USER").build();
        when(planes.findById(1L)).thenReturn(Optional.of(plan));
        when(roles.findByNombreIgnoreCase("USER")).thenReturn(Optional.of(user));
        when(encoder.encode("alejandra.2005")).thenReturn("hash");
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario value = invocation.getArgument(0); value.setId(3L); return value;
        });
        when(suscripciones.save(any(Suscripcion.class))).thenAnswer(invocation -> {
            Suscripcion value = invocation.getArgument(0); value.setId(4L); return value;
        });

        var response = service.registrar(request);

        assertEquals("USER", response.usuario().rol());
        assertEquals(30, response.suscripcion().fechaExpiracion().toEpochDay()
            - response.suscripcion().fechaInicio().toEpochDay());
    }
}
