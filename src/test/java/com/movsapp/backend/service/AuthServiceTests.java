package com.movsapp.backend.service;

import com.movsapp.backend.dto.LoginRequest;
import com.movsapp.backend.entity.*;
import com.movsapp.backend.exception.*;
import com.movsapp.backend.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTests {
    @Mock UsuarioRepository usuarios;
    @Mock SesionRepository sesiones;
    @Mock PasswordEncoder encoder;
    @InjectMocks AuthService service;
    Usuario usuario;

    @BeforeEach void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = Usuario.builder().id(1L).correo("demo@movs.app").passwordHash("hash")
            .rol(Rol.builder().id(2L).nombre("usuario").build()).build();
    }

    @Test void rechazaCredencialesInvalidasSinExponerElMotivo() {
        when(usuarios.findByCorreoIgnoreCase("demo@movs.app")).thenReturn(Optional.of(usuario));
        when(encoder.matches("incorrecta", "hash")).thenReturn(false);
        NoAutorizadoException error = assertThrows(NoAutorizadoException.class,
            () -> service.login(new LoginRequest("demo@movs.app", "incorrecta")));
        assertEquals("Correo o contraseña incorrectos.", error.getMessage());
    }

    @Test void rechazaUnaSegundaSesionActiva() {
        Sesion activa = Sesion.builder().id(1L).usuario(usuario).activa(true).build();
        when(usuarios.findByCorreoIgnoreCase("demo@movs.app")).thenReturn(Optional.of(usuario));
        when(encoder.matches("usuario123", "hash")).thenReturn(true);
        when(sesiones.findForUpdateByUsuarioId(1L)).thenReturn(Optional.of(activa));
        ConflictoException error = assertThrows(ConflictoException.class,
            () -> service.login(new LoginRequest("demo@movs.app", "usuario123")));
        assertEquals(AuthService.SESION_DUPLICADA, error.getMessage());
        verify(sesiones, never()).save(any());
    }
}
