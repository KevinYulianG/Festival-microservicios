package com.festival.ms_usuario.mapper;

import com.festival.ms_usuario.dto.UsuarioDto;
import com.festival.ms_usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDto toDTO(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        dto.setActivo(usuario.getActivo());
        return dto;
    }

    public Usuario toEntity(UsuarioDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setActivo(true);
        return usuario;
    }
}