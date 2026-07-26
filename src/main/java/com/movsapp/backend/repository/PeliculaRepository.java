package com.movsapp.backend.repository;
import com.movsapp.backend.entity.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByGeneroIgnoreCase(String genero);
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
    @Query("""
        select p from Pelicula p
        where :busqueda = ''
           or lower(p.titulo) like lower(concat('%', :busqueda, '%'))
           or lower(p.genero) like lower(concat('%', :busqueda, '%'))
        """)
    Page<Pelicula> buscarPagina(@Param("busqueda") String busqueda, Pageable pageable);
}
