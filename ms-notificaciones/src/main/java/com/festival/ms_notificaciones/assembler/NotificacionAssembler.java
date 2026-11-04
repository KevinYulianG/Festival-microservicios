package com.festival.ms_notificaciones.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.festival.ms_notificaciones.controller.NotificacionesControllerV2;
import com.festival.ms_notificaciones.dto.NotificacionDTO;

@Component
public class NotificacionAssembler
    implements RepresentationModelAssembler<NotificacionDTO, EntityModel<NotificacionDTO>> {

    @Override
    public EntityModel<NotificacionDTO> toModel(NotificacionDTO dto) {
        return EntityModel.of(dto,
            linkTo(methodOn(NotificacionesControllerV2.class)
                .listarPorUsuario(dto.getUsuarioId())).withSelfRel(),
            linkTo(methodOn(NotificacionesControllerV2.class)
                .listarPorTipo(dto.getTipo())).withRel("notificacionesPorTipo"),
            linkTo(methodOn(NotificacionesControllerV2.class)
                .eliminar(dto.getId())).withRel("eliminar")
        );
    }
}