package com.movsapp.backend.repository;

import com.movsapp.backend.entity.Favorito;
import com.movsapp.backend.entity.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {
    List<Favorito> findByUsuarioIdOrderByFechaAgregadaDesc(Long usuarioId);
}
