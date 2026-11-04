package com.festival.ms_evento.assembler;

import com.festival.ms_evento.DTO.EventoDto;
import com.festival.ms_evento.controller.EventoController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventoAssembler implements RepresentationModelAssembler<EventoDto, EntityModel<EventoDto>> {

    @Override
    public EntityModel<EventoDto> toModel(EventoDto dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EventoController.class).buscarPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(EventoController.class).listar()).withRel("todosLosEventos"),
                linkTo(methodOn(EventoController.class).listarPublicados()).withRel("eventosPublicados"),
                linkTo(methodOn(EventoController.class).isPublicado(dto.getId())).withRel("estaPublicado")
        );
    }
}
