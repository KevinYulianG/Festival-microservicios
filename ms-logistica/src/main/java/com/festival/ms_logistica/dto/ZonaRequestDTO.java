package com.festival.ms_logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRequestDTO {

    @NotBlank(message = "Es obligatorio ingresar un nombre")
    private String nombre;

    @NotBlank(message = "Es obligatorio ingresar el tipo de zona")
    private String tipo;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;
}