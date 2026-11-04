package com.festival.ms_logistica.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EscenarioRequestDTO {

    @NotBlank(message = "Es obligatorio ingresar un nombre")
    private String nombre;

    @NotNull(message = "Es obligatorio ingresar la capacidad de usuarios")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacidad;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;
}