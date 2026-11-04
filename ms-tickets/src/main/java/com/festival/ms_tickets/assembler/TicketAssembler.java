package com.festival.ms_tickets.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_tickets.controller.v2.TicketControllerV2;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;

@Component
public class TicketAssembler
    implements RepresentationModelAssembler<TicketRespuestaDTO, EntityModel<TicketRespuestaDTO>> {

    @Override
    public EntityModel<TicketRespuestaDTO> toModel(TicketRespuestaDTO dto) {
        return EntityModel.of(dto,

            linkTo(methodOn(TicketControllerV2.class)
                .obtenerTicket(dto.getId())).withSelfRel(),

            linkTo(methodOn(TicketControllerV2.class)
                .obtenerPorUsuario(dto.getUsuarioId())).withRel("ticketsPorUsuario"),

            linkTo(methodOn(TicketControllerV2.class)
                .obtenerPorEvento(dto.getEventoId())).withRel("ticketsPorEvento"),

            linkTo(methodOn(TicketControllerV2.class)
                .validarEntrada(dto.getCodigoQr())).withRel("validarEntrada"),

            linkTo(methodOn(TicketControllerV2.class)
                .cancelarTicket(dto.getId())).withRel("cancelar")
        );
    }
}