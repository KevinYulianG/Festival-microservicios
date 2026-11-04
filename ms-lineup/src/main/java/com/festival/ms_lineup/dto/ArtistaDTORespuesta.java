package com.festival.ms_lineup.dto;

import com.festival.ms_lineup.model.EstadoArtista;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistaDTORespuesta {

    private Long id;
    private String nombre;
    private String generoMusical;
    private String paisOrigen;
    private String descripcion;
    private EstadoArtista estado;
}