package com.festival.ms_evento.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.festival.ms_evento.model.EstadoEvento;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventoDto {

    // Solo de salida: el cliente no puede enviar el ID
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
    private String descripcion;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String ubicacion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El aforo maximo es obligatorio")
    @Min(value = 1, message = "El aforo debe ser al menos 1 persona")
    @Max(value = 500000, message = "El aforo no puede superar 500000")
    private Integer aforoMaximo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EstadoEvento estado;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime creadoEn;
}