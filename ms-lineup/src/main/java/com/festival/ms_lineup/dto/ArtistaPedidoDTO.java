package com.festival.ms_lineup.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArtistaPedidoDTO {

    @NotBlank(message = "El nombre del artista es obligatorio")
    private String nombre;

    @NotBlank(message = "El género musical es obligatorio")
    private String generoMusical;

    @NotBlank(message = "El país de origen es obligatorio")
    private String paisOrigen;

    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private String estado; // ACTIVO, INACTIVO, CANCELADO
}