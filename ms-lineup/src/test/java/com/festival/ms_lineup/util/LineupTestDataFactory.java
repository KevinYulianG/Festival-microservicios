package com.festival.ms_lineup.util;

import java.time.LocalDateTime;

import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;
import com.festival.ms_lineup.dto.EventoDTORespuesta;
import com.festival.ms_lineup.model.EstadoArtista;
import com.festival.ms_lineup.model.EstadoProgramacion;

public class LineupTestDataFactory {


    // ARTISTA - DTOs de entrada (request)

    public static ArtistaPedidoDTO artistaPedidoFalso() {
        ArtistaPedidoDTO dto = new ArtistaPedidoDTO();
        dto.setNombre("Bad Bunny");
        dto.setGeneroMusical("Reggaeton");
        dto.setPaisOrigen("Puerto Rico");
        dto.setDescripcion("Artista urbano internacional");
        dto.setEstado("ACTIVO");
        return dto;
    }

    // ARTISTA - DTOs de respuesta (response)

    public static ArtistaDTORespuesta artistaRespuestaActivo() {
        return ArtistaDTORespuesta.builder()
            .id(1L)
            .nombre("Bad Bunny")
            .generoMusical("Reggaeton")
            .paisOrigen("Puerto Rico")
            .descripcion("Artista urbano internacional")
            .estado(EstadoArtista.ACTIVO)
            .build();
    }

    public static ArtistaDTORespuesta artistaRespuestaInactivo() {
        return ArtistaDTORespuesta.builder()
            .id(2L)
            .nombre("Artista Retirado")
            .generoMusical("Rock")
            .paisOrigen("Chile")
            .descripcion("Artista ya no activo")
            .estado(EstadoArtista.INACTIVO)
            .build();
    }

    public static ArtistaDTORespuesta artistaRespuestaCancelado() {
        return ArtistaDTORespuesta.builder()
            .id(3L)
            .nombre("Coldplay")
            .generoMusical("Rock Alternativo")
            .paisOrigen("Reino Unido")
            .descripcion("Cancelo su presentacion")
            .estado(EstadoArtista.CANCELADO)
            .build();
    }

    // PROGRAMACION - DTOs de entrada (request)

    public static ProgramacionPedidoDTO programacionPedidoFalso() {
        ProgramacionPedidoDTO dto = new ProgramacionPedidoDTO();
        dto.setArtistaId(1L);
        dto.setEventoId(1L);
        dto.setNombreEscenario("Escenario Principal");
        dto.setHoraInicio(LocalDateTime.of(2025, 8, 15, 21, 0));
        dto.setHoraFin(LocalDateTime.of(2025, 8, 15, 23, 0));
        return dto;
    }

    public static ProgramacionPedidoDTO programacionPedidoHoraInvalida() {
        ProgramacionPedidoDTO dto = new ProgramacionPedidoDTO();
        dto.setArtistaId(1L);
        dto.setEventoId(1L);
        dto.setNombreEscenario("Escenario Principal");
        // Hora fin antes que hora inicio (invalido)
        dto.setHoraInicio(LocalDateTime.of(2025, 8, 15, 23, 0));
        dto.setHoraFin(LocalDateTime.of(2025, 8, 15, 21, 0));
        return dto;
    }

    // PROGRAMACION - DTOs de respuesta (response)

    public static ProgramacionDTORespuesta programacionRespuestaProgramado() {
        return ProgramacionDTORespuesta.builder()
            .id(1L)
            .artistaId(1L)
            .nombreArtista("Bad Bunny")
            .eventoId(1L)
            .nombreEscenario("Escenario Principal")
            .horaInicio(LocalDateTime.of(2025, 8, 15, 21, 0))
            .horaFin(LocalDateTime.of(2025, 8, 15, 23, 0))
            .estado(EstadoProgramacion.PROGRAMADO)
            .build();
    }

    public static ProgramacionDTORespuesta programacionRespuestaFinalizado() {
        return ProgramacionDTORespuesta.builder()
            .id(2L)
            .artistaId(2L)
            .nombreArtista("Coldplay")
            .eventoId(1L)
            .nombreEscenario("Escenario Secundario")
            .horaInicio(LocalDateTime.of(2025, 8, 14, 18, 0))
            .horaFin(LocalDateTime.of(2025, 8, 14, 20, 0))
            .estado(EstadoProgramacion.FINALIZADO)
            .build();
    }

    public static ProgramacionDTORespuesta programacionRespuestaCancelado() {
        return ProgramacionDTORespuesta.builder()
            .id(3L)
            .artistaId(1L)
            .nombreArtista("Bad Bunny")
            .eventoId(1L)
            .nombreEscenario("Escenario Principal")
            .horaInicio(LocalDateTime.of(2025, 8, 16, 21, 0))
            .horaFin(LocalDateTime.of(2025, 8, 16, 23, 0))
            .estado(EstadoProgramacion.CANCELADO)
            .build();
    }

    // DTOs de clientes externos (Feign)

    public static EventoDTORespuesta eventoPublicadoFalso() {
        EventoDTORespuesta dto = new EventoDTORespuesta();
        dto.setId(1L);
        dto.setNombre("Festival Reggae 2025");
        dto.setEstado("PUBLICADO");
        dto.setAforoMaximo(3000);
        return dto;
    }

    public static EventoDTORespuesta eventoNoPuplicadoFalso() {
        EventoDTORespuesta dto = new EventoDTORespuesta();
        dto.setId(2L);
        dto.setNombre("Festival Metal 2025");
        dto.setEstado("BORRADOR");
        dto.setAforoMaximo(6000);
        return dto;
    }
}