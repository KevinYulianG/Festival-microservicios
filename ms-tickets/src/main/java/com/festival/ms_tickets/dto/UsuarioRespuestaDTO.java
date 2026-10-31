package com.festival.ms_tickets.dto;

import lombok.Data;

@Data
public class UsuarioRespuestaDTO {

    private Long id;
    private String nombre;
    private String email;
    private boolean activo;
    private String nombreRol; // para verificar permisos si es necesario

}
