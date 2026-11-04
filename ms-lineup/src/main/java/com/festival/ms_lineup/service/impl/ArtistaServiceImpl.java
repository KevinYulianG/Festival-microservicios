package com.festival.ms_lineup.service.impl;

import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;
import com.festival.ms_lineup.exception.ArtistaNoEncontrado;
import com.festival.ms_lineup.mapper.LineupMapper;
import com.festival.ms_lineup.model.Artista;
import com.festival.ms_lineup.model.EstadoArtista;
import com.festival.ms_lineup.repository.ArtistaRepository;
import com.festival.ms_lineup.service.ArtistaService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistaServiceImpl implements ArtistaService {

    private static final Logger log = LoggerFactory.getLogger(ArtistaServiceImpl.class);

    private final ArtistaRepository artistaRepository;
    private final LineupMapper lineupMapper;

    @Override
    public ArtistaDTORespuesta crearArtista(ArtistaPedidoDTO dto) {

        // Verificar que no exista artista con el mismo nombre
        if (artistaRepository.existsByNombre(dto.getNombre())) {
            log.warn("Artista ya existe con nombre: {}", dto.getNombre());
            throw new ArtistaNoEncontrado(
                "Ya existe un artista con el nombre: " + dto.getNombre());
        }

        Artista artista = artistaRepository.save(
            Artista.builder()
                .nombre(dto.getNombre())
                .generoMusical(dto.getGeneroMusical())
                .paisOrigen(dto.getPaisOrigen())
                .descripcion(dto.getDescripcion())
                .estado(EstadoArtista.ACTIVO)
                .build()
        );

        log.info("Artista creado exitosamente - nombre: {}", dto.getNombre());
        return lineupMapper.toArtistaDTO(artista);
    }

    @Override
    public ArtistaDTORespuesta obtenerArtista(Long id) {
        Artista artista = artistaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Artista no encontrado - ID: {}", id);
                return new ArtistaNoEncontrado(
                    "Artista no encontrado con ID: " + id);
            });
        return lineupMapper.toArtistaDTO(artista);
    }

    @Override
    public List<ArtistaDTORespuesta> listarArtistas() {
        log.info("Listando todos los artistas");
        return lineupMapper.toArtistaDTOList(artistaRepository.findAll());
    }

    @Override
    public ArtistaDTORespuesta actualizarArtista(Long id, ArtistaPedidoDTO dto) {
        Artista artista = artistaRepository.findById(id)
            .orElseThrow(() -> new ArtistaNoEncontrado(
                "Artista no encontrado con ID: " + id));

        artista.setNombre(dto.getNombre());
        artista.setGeneroMusical(dto.getGeneroMusical());
        artista.setPaisOrigen(dto.getPaisOrigen());
        artista.setDescripcion(dto.getDescripcion());

        artistaRepository.save(artista);
        log.info("Artista actualizado - ID: {}", id);
        return lineupMapper.toArtistaDTO(artista);
    }

    @Override
    public ArtistaDTORespuesta desactivarArtista(Long id) {
        Artista artista = artistaRepository.findById(id)
            .orElseThrow(() -> new ArtistaNoEncontrado(
                "Artista no encontrado con ID: " + id));

        artista.setEstado(EstadoArtista.INACTIVO);
        artistaRepository.save(artista);
        log.info("Artista desactivado - ID: {}", id);
        return lineupMapper.toArtistaDTO(artista);
    }
}