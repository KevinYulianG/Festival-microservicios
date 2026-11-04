package com.festival.ms_evento.mapper;

import com.festival.ms_evento.DTO.EventoDto;
import com.festival.ms_evento.model.Evento;
import com.festival.ms_evento.model.EstadoEvento;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    // Entidad → DTO (para respuestas)
    public EventoDto toDTO(Evento evento) {
        EventoDto dto = new EventoDto();
        dto.setId(evento.getId());
        dto.setNombre(evento.getNombre());
        dto.setDescripcion(evento.getDescripcion());
        dto.setUbicacion(evento.getUbicacion());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        dto.setAforoMaximo(evento.getAforoMaximo());
        dto.setEstado(evento.getEstado());
        dto.setCreadoEn(evento.getCreadoEn());
        return dto;
    }

    public Evento toEntity(EventoDto dto) {
        Evento evento = new Evento();
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setUbicacion(dto.getUbicacion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setAforoMaximo(dto.getAforoMaximo());
        evento.setEstado(EstadoEvento.BORRADOR);
        return evento;
    }

    public void updateEntity(Evento evento, EventoDto dto) {
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setUbicacion(dto.getUbicacion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());
        evento.setAforoMaximo(dto.getAforoMaximo());
    }
}