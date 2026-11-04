package com.festival.ms_lineup.mapper;


import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.model.Artista;
import com.festival.ms_lineup.model.ProgramacionArtista;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LineupMapper {

    public ArtistaDTORespuesta toArtistaDTO(Artista artista) {
        return ArtistaDTORespuesta.builder()
            .id(artista.getId())
            .nombre(artista.getNombre())
            .generoMusical(artista.getGeneroMusical())
            .paisOrigen(artista.getPaisOrigen())
            .descripcion(artista.getDescripcion())
            .estado(artista.getEstado())
            .build();
    }

    public ProgramacionDTORespuesta toProgramacionDTO(ProgramacionArtista prog) {
        return ProgramacionDTORespuesta.builder()
            .id(prog.getId())
            .artistaId(prog.getArtista().getId())
            .nombreArtista(prog.getArtista().getNombre())
            .eventoId(prog.getEventoId())
            .nombreEscenario(prog.getNombreEscenario())
            .horaInicio(prog.getHoraInicio())
            .horaFin(prog.getHoraFin())
            .estado(prog.getEstado())
            .build();
    }

    public List<ArtistaDTORespuesta> toArtistaDTOList(List<Artista> artistas) {
        return artistas.stream()
            .map(this::toArtistaDTO)
            .collect(Collectors.toList());
    }

    public List<ProgramacionDTORespuesta> toProgramacionDTOList(
            List<ProgramacionArtista> programaciones) {
        return programaciones.stream()
            .map(this::toProgramacionDTO)
            .collect(Collectors.toList());
    }
}