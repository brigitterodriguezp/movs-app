package com.movsapp.backend.repository;
import com.movsapp.backend.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByUsuarioId(Long usuarioId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sesion> findForUpdateByUsuarioId(Long usuarioId);
}
