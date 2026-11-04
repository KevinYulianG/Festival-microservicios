package com.festival.ms_logistica.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionStaffRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "La labor es obligatoria")
    private String labor;
}