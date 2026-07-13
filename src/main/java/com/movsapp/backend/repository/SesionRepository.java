package com.movsapp.backend.repository;
import com.movsapp.backend.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    Optional<Sesion> findByUsuarioId(Long usuarioId);
    @Query("select s from Sesion s join fetch s.usuario u join fetch u.rol where s.id = :id")
    Optional<Sesion> findWithUsuarioById(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sesion> findForUpdateByUsuarioId(Long usuarioId);
}
