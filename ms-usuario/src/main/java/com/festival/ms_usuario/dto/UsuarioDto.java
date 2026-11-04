package com.festival.ms_usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UsuarioDto {

    
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    private String email;

    
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    
    private String rolNombre;

    private Boolean activo;
}