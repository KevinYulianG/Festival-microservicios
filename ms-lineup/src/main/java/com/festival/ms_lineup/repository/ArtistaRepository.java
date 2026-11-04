package com.festival.ms_lineup.repository;

import com.festival.ms_lineup.model.Artista;
import com.festival.ms_lineup.model.EstadoArtista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    List<Artista> findByEstado(EstadoArtista estado);

    boolean existsByNombre(String nombre);

    List<Artista> findByGeneroMusical(String generoMusical);
}