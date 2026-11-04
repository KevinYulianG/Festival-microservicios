package com.festival.ms_logistica.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_logistica.controller.EscenarioControllerV2;
import com.festival.ms_logistica.dto.EscenarioDTO;

@Component
public class EscenarioAssembler implements RepresentationModelAssembler<EscenarioDTO, EntityModel<EscenarioDTO>> {

    @Override
    public EntityModel<EscenarioDTO> toModel(EscenarioDTO dto) {
        return EntityModel.of(dto,
            
            linkTo(methodOn(EscenarioControllerV2.class)
                .actualizarEscenario(dto.getId(), dto)).withSelfRel(),
            
            
            linkTo(methodOn(EscenarioControllerV2.class)
                .actualizarEscenario(dto.getId(), dto)).withRel("actualizar")
        );
    }
}