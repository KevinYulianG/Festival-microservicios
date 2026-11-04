package com.festival.ms_usuario.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EventoDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer aforoMaximo;
    private String estado;
}
