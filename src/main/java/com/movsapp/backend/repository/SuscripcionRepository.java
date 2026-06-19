package com.movsapp.backend.repository;
import com.movsapp.backend.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    Optional<Suscripcion> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}
