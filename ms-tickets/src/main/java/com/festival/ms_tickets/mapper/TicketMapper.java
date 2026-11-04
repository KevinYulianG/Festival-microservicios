package com.festival.ms_tickets.mapper;

import org.springframework.stereotype.Component;

import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.model.Tickets;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketMapper {

    // Convierte la entidad Ticket a TicketRespuestaDTO
    public TicketRespuestaDTO toDTO(Tickets ticket) {
        return TicketRespuestaDTO.builder()
            .id(ticket.getId())
            .usuarioId(ticket.getUsuarioId())
            .eventoId(ticket.getEventoId())
            .tipoEntrada(ticket.getTipoEntrada())
            .codigoQr(ticket.getCodigoQr())
            .estado(ticket.getEstado())
            .precioPagado(ticket.getPrecioPagado())
            .fechaCompra(ticket.getFechaCompra())
            .fechaValidacion(ticket.getFechaValidacion())
            .build();
    }

    // Convierte la lista de entidades a lista de DTOs
    public List<TicketRespuestaDTO> toDTOList(List<Tickets> tickets) {
        return tickets.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}