package com.movsapp.backend.repository;

import com.movsapp.backend.entity.Favorito;
import com.movsapp.backend.entity.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {
    List<Favorito> findByUsuarioIdOrderByFechaAgregadaDesc(Long usuarioId);

    @EntityGraph(attributePaths = "pelicula")
    @Query("""
        select f from Favorito f join f.pelicula p
        where f.usuario.id = :usuarioId
          and (:busqueda = ''
            or lower(p.titulo) like lower(concat('%', :busqueda, '%'))
            or lower(p.genero) like lower(concat('%', :busqueda, '%')))
        order by f.fechaAgregada desc
        """)
    Page<Favorito> buscarPagina(@Param("usuarioId") Long usuarioId,
                                @Param("busqueda") String busqueda,
                                Pageable pageable);
}
