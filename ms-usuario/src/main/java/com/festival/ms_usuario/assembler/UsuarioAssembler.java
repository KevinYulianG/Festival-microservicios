package com.festival.ms_usuario.assembler;

import com.festival.ms_usuario.controller.UsuarioController;
import com.festival.ms_usuario.dto.UsuarioDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioAssembler
    implements RepresentationModelAssembler<UsuarioDto, EntityModel<UsuarioDto>> {

    @Override
    public EntityModel<UsuarioDto> toModel(UsuarioDto dto) {
        return EntityModel.of(dto,

            // Link a sí mismo
            linkTo(methodOn(UsuarioController.class)
                .buscarPorId(dto.getId())).withSelfRel(),

            // Link a todos los usuarios
            linkTo(methodOn(UsuarioController.class)
                .listar()).withRel("todosLosUsuarios"),

            // Link a eventos disponibles para este usuario
            linkTo(methodOn(UsuarioController.class)
                .obtenerEventosDisponibles()).withRel("eventosDisponibles"),

            // Link para desactivar el usuario
            linkTo(methodOn(UsuarioController.class)
                .desactivar(dto.getId())).withRel("desactivar")
        );
    }
}
