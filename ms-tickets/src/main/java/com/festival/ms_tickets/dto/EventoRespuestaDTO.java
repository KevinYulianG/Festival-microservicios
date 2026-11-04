package com.festival.ms_tickets.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventoRespuestaDTO {

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