package com.festival.ms_logistica.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_logistica.controller.ZonaControllerV2;
import com.festival.ms_logistica.dto.ZonaDTO;

@Component
public class ZonaAssembler implements RepresentationModelAssembler<ZonaDTO, EntityModel<ZonaDTO>> {

    @Override
    public EntityModel<ZonaDTO> toModel(ZonaDTO dto) {
        return EntityModel.of(dto,

                linkTo(methodOn(ZonaControllerV2.class)
                        .listarZonasPorEvento(dto.getEventoId())).withSelfRel(),

                linkTo(methodOn(ZonaControllerV2.class)
                        .actualizarZona(dto.getId(), dto)).withRel("actualizar"),

                linkTo(methodOn(ZonaControllerV2.class)
                        .eliminarZona(dto.getId())).withRel("eliminar"));
    }
}