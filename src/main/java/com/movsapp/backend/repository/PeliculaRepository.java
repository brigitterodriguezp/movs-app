package com.movsapp.backend.repository;
import com.movsapp.backend.entity.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByGeneroIgnoreCase(String genero);
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
}
