package com.festival.ms_lineup.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventoDTORespuesta {

    private Long id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String estado;
    private Integer aforoMaximo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime creadoEn;
}