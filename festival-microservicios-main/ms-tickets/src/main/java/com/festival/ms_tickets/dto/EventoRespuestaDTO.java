package com.festival.ms_tickets.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventoRespuestaDTO {

    private Long id;
    private String nombre;
    private String estado;       // "PUBLICADO", "BORRADOR", "CANCELADO", "FINALIZADO"
    private Integer capacidad;   // aforo máximo del evento
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
}
