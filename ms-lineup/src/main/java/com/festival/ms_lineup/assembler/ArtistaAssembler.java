package com.festival.ms_lineup.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_lineup.controller.v2.ArtistaControllerV2;
import com.festival.ms_lineup.controller.v2.ProgramacionControllerV2;
import com.festival.ms_lineup.dto.ArtistaDTORespuesta;

@Component
public class ArtistaAssembler
    implements RepresentationModelAssembler<ArtistaDTORespuesta, EntityModel<ArtistaDTORespuesta>> {

    @Override
    public EntityModel<ArtistaDTORespuesta> toModel(ArtistaDTORespuesta dto) {
        return EntityModel.of(dto,

            // Link al propio artista por ID
            linkTo(methodOn(ArtistaControllerV2.class)
                .obtenerArtista(dto.getId())).withSelfRel(),

            // Link a la lista completa de artistas
            linkTo(methodOn(ArtistaControllerV2.class)
                .listarArtistas()).withRel("todosLosArtistas"),

            // Link a las programaciones de este artista
            linkTo(methodOn(ProgramacionControllerV2.class)
                .obtenerPorArtista(dto.getId())).withRel("programaciones")
        );
    }
}