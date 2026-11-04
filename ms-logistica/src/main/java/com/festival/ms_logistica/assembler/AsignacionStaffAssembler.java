package com.festival.ms_logistica.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_logistica.controller.AsignacionStaffControllerV2;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;

@Component
public class AsignacionStaffAssembler
    implements RepresentationModelAssembler<AsignacionStaffDTO, EntityModel<AsignacionStaffDTO>> {

    @Override
    public EntityModel<AsignacionStaffDTO> toModel(AsignacionStaffDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .listarStaffPorZona(dto.getZonaId())).withSelfRel(),
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .actualizarAsignacion(dto.getId(), null)).withRel("actualizar"),
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .eliminarAsignacion(dto.getId())).withRel("eliminar"),
            linkTo(methodOn(AsignacionStaffControllerV2.class)
                .listarStaffPorZona(dto.getZonaId())).withRel("staffPorZona")
        );
    }
}