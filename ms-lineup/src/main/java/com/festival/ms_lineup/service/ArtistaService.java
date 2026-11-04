package com.festival.ms_lineup.service;

import java.util.List;
import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;

public interface ArtistaService {

    ArtistaDTORespuesta crearArtista(ArtistaPedidoDTO dto);

    ArtistaDTORespuesta obtenerArtista(Long id);

    List<ArtistaDTORespuesta> listarArtistas();

    ArtistaDTORespuesta actualizarArtista(Long id, ArtistaPedidoDTO dto);

    ArtistaDTORespuesta desactivarArtista(Long id);
}