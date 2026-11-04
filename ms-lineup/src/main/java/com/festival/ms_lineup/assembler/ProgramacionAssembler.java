package com.festival.ms_lineup.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_lineup.controller.v2.ProgramacionControllerV2;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;

@Component
public class ProgramacionAssembler
    implements RepresentationModelAssembler<ProgramacionDTORespuesta, EntityModel<ProgramacionDTORespuesta>> {

    @Override
    public EntityModel<ProgramacionDTORespuesta> toModel(ProgramacionDTORespuesta dto) {
        return EntityModel.of(dto,

            // Link a la propia programacion por ID
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerProgramacion(dto.getId())).withSelfRel(),

            // Link a todas las programaciones del mismo evento
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerPorEvento(dto.getEventoId())).withRel("programacionesDelEvento"),

            // Link a todas las programaciones del mismo artista
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerPorArtista(dto.getArtistaId())).withRel("programacionesDelArtista"),

            // Link para cancelar esta programacion
            linkTo(methodOn(ProgramacionControllerV2.class)
                .cancelarProgramacion(dto.getId())).withRel("cancelar"),

            // Link para actualizar el horario
            linkTo(methodOn(ProgramacionControllerV2.class)
                .actualizarHorario(dto.getId(), null)).withRel("actualizarHorario")
        );
    }
}