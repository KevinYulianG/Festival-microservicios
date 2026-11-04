package com.festival.ms_lineup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProgramacionPedidoDTO {

    @NotNull(message = "El ID del artista es obligatorio")
    private Long artistaId;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El nombre del escenario es obligatorio")
    private String nombreEscenario;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDateTime horaFin;
}